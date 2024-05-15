package me.msicraft.ctcollection.Event;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.Manager.CollectionManager;
import me.msicraft.ctcollection.Menu.CollectionInventory;
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

import java.util.Set;

public class PlayerRelatedEvent implements Listener {

    private final CTCollection plugin;

    public PlayerRelatedEvent(CTCollection plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDataLoad(PlayerDataLoadEvent e) {
        Player player = e.getPlayer();

        PlayerData playerData = e.getPlayerData();
        playerData.loadTagData("CTCollection.CollectionInfo");

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

        Set<Material> registeredMaterials = collectionManager.getCollectionMaterials();
        for (Material material : registeredMaterials) {
            collectionInventory.setCollectionInfo(material, new Pair<>(false, 0));
        }

        TagData tagData = playerData.getTagData("CollectionInfo");
        if (tagData != null) {
            byte[] bytes = Base64Util.stringToByteArray((String) tagData.getValue());
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

        collectionInventory.updateCollection();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        plugin.getCollectionManager().removeCachedInventory(player);
    }

}
