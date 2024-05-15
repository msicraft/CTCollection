package me.msicraft.ctcollection.Menu;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.Manager.CollectionManager;
import me.msicraft.ctcollection.aCommon.Collection;
import me.msicraft.ctcore.Utils.Base64Util;
import me.msicraft.ctcore.Utils.GuiUtil;
import me.msicraft.ctcore.aCommon.Pair;
import me.msicraft.ctplayerdata.CTPlayerData;
import me.msicraft.ctplayerdata.PlayerData.PlayerData;
import me.msicraft.ctplayerdata.PlayerData.aCommon.TagData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CollectionInventory implements InventoryHolder {

    private final Player player;;
    private final Inventory inventory;
    private final CTCollection plugin;

    private final Map<Material, Pair<Boolean, Integer>> collectionInfoMap = new HashMap<>(); //재료, <수집여부, 갯수>
    private int completeCount = 0;

    private int pageCount = 0;

    public CollectionInventory(CTCollection plugin, Player player) {
        CollectionManager collectionManager = plugin.getCollectionManager();

        this.player = player;
        this.plugin = plugin;
        Pair<CollectionInventory, Inventory> pair = collectionManager.getCachedInventory(player);
        this.inventory = pair.getV2();
    }

    public void updateCollection() {
        CollectionManager collectionManager = plugin.getCollectionManager();
        Set<Integer> ids = collectionManager.getCollectionIds();
        for (int i : ids) {
            Collection collection = collectionManager.getCollection(i);
            int amount = collection.getAmount();
            if (collectionInfoMap.containsKey(collection.getMaterial())) {
                Pair<Boolean, Integer> pair = collectionInfoMap.get(collection.getMaterial());
                if (!pair.getV1()) {
                    int count = pair.getV2();
                    if (count >= amount) {
                        pair.setV1(true);
                        collectionInfoMap.put(collection.getMaterial(), pair);
                        completeCount++;
                    }
                } else {
                    completeCount++;
                }
            }
        }

        PlayerData playerData = CTPlayerData.getPlugin().getPlayerDataManager().getPlayerData(player);
        TagData tagData = playerData.getTagData("CollectionInfo");
        if (tagData == null) {
            tagData = new TagData("CTCollection.CollectionInfo", "");
        }
        tagData.setValue(getCollectionCountEncodeData());

        playerData.setTagData("CollectionInfo", tagData);
    }

    public String getCollectionCountEncodeData() {
        String s = "";
        for (Material material : collectionInfoMap.keySet()) {
            Pair<Boolean, Integer> pair = collectionInfoMap.get(material);
            boolean isComplete = pair.getV1();
            int count = pair.getV2();
            if (s.isEmpty()) {
                s = material.name() + "=" + isComplete + "=" + count;
            } else {
                s = s + ":" + material.name() + "=" + isComplete + "=" + count;
            }
        }
        return Base64Util.byteArrayToString(s.getBytes());
    }

    public void open(Player player) {
        player.openInventory(inventory);
        inventory.clear();

        setUp(CTPlayerData.getPlugin().getPlayerDataManager().getPlayerData(player));

        player.updateInventory();
    }

    private void setUp(PlayerData playerData) {
        String key = "CT_Collection_Menu";
        CollectionManager collectionManager = plugin.getCollectionManager();
        ItemStack itemStack = null;
        int maxSize = collectionManager.getCollectionIds().size();
        int maxPageSize = maxSize / 45;
        int guiCount = 0;
        int lastCount = pageCount * 45;
        for (int i = lastCount; i < maxSize; i++) {
            if (collectionManager.hasCollection(i)) {
                Collection collection = collectionManager.getCollection(i);
                Pair<Boolean, Integer> pair = collectionInfoMap.get(collection.getMaterial());
                /*
                TagData tagData = playerData.getTagData("CollectionInfo");
                boolean isComplete = false;
                if (tagData != null) {
                    isComplete = (boolean) tagData.getValue();
                }
                itemStack = collection.getCollectionStack(isComplete);

                 */
                this.inventory.setItem(guiCount, itemStack);
                guiCount++;
            }
            if (guiCount >= 45) {
                break;
            }
        }
        String pageS = "페이지: " + (pageCount + 1) + "/" + (maxPageSize + 1);
        itemStack = GuiUtil.createItemStack(plugin, Material.BOOK, pageS, GuiUtil.EMPTY_LORE, -1, key, "page");
        inventory.setItem(49, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1, key, "next");
        inventory.setItem(50, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1, key, "previous");
        inventory.setItem(48, itemStack);
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public Pair<Boolean, Integer> getCollectionInfo(Material material) {
        return collectionInfoMap.get(material);
    }

    public void setCollectionInfo(Material material, Pair<Boolean, Integer> pair) {
        collectionInfoMap.put(material, pair);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}
