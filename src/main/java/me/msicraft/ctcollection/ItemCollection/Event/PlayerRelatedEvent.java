package me.msicraft.ctcollection.ItemCollection.Event;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.ItemCollection.Manager.CollectionManager;
import me.msicraft.ctcollection.ItemCollection.Menu.CollectionInventory;
import me.msicraft.ctcore.Utils.Base64Util;
import me.msicraft.ctcore.aCommon.Pair;
import me.msicraft.ctplayerdata.PlayerData.CustomEvent.PlayerDataLoadEvent;
import me.msicraft.ctplayerdata.PlayerData.PlayerData;
import me.msicraft.ctplayerdata.PlayerData.aCommon.TagData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class PlayerRelatedEvent implements Listener {

    private final CTCollection plugin;

    public PlayerRelatedEvent(CTCollection plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDataLoad(PlayerDataLoadEvent e) {
        Player player = e.getPlayer();

        PlayerData playerData = e.getPlayerData();
        playerData.loadTagData("CTCollection");

        Pair<CollectionInventory, Inventory> pair = plugin.getCollectionManager().getCachedInventory(player);
        CollectionInventory collectionInventory;
        if (pair == null) {
            collectionInventory = new CollectionInventory(plugin, player);
            pair = new Pair<>(collectionInventory, collectionInventory.getInventory());
        } else {
            collectionInventory = pair.getV1();
        }
        plugin.getCollectionManager().setCachedInventory(player, pair);

        CollectionManager collectionManager = plugin.getCollectionManager();

        List<Material> registeredMaterials = collectionManager.getCollectionMaterials();
        for (Material material : registeredMaterials) {
            collectionInventory.setCollectionInfo(material, new Pair<>(false, 0));
        }

        TagData tagData = playerData.getTagData("CollectionInfo");
        if (tagData != null) {
            String v = (String) tagData.getValue();
            if (!v.isEmpty()) {
                byte[] bytes = Base64Util.stringToByteArray(v);
                String decodeS = new String(bytes);
                String[] a = decodeS.split(":");
                for (String s : a) {
                    String[] b = s.split("=");
                    Material material = Material.getMaterial(b[0].toUpperCase());
                    if (material != null && registeredMaterials.contains(material)) {
                        Pair<Boolean, Integer> p = collectionInventory.getCollectionInfo(material);
                        p.setV1(Boolean.parseBoolean(b[1]));
                        p.setV2(Integer.parseInt(b[2]));
                        collectionInventory.setCollectionInfo(material, p);
                    }
                }
            }
        }

        collectionInventory.updateCollection();

        TagData rewardTagData = playerData.getTagData("RewardInfo");
        if (rewardTagData != null) {
            String v = (String) rewardTagData.getValue();
            if (!v.isEmpty()) {
                byte[] bytes = Base64Util.stringToByteArray(v);
                String decodeS = new String(bytes);
                String[] a = decodeS.split(":");
                for (String s : a) {
                    String[] b = s.split("=");
                    int count = Integer.parseInt(b[0]);
                    boolean check = Boolean.parseBoolean(b[1]);
                    collectionInventory.setRewardReceived(count, check);
                }
            }
        }

        collectionInventory.updateRewardInfo();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        plugin.getCollectionManager().removeCachedInventory(player);
    }

}
