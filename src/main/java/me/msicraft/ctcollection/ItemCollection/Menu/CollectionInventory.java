package me.msicraft.ctcollection.ItemCollection.Menu;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.ItemCollection.Collection;
import me.msicraft.ctcollection.ItemCollection.Manager.CollectionManager;
import me.msicraft.ctcollection.Reward.Manager.RewardManager;
import me.msicraft.ctcore.Utils.Base64Util;
import me.msicraft.ctcore.Utils.GuiUtil;
import me.msicraft.ctcore.aCommon.Pair;
import me.msicraft.ctplayerdata.CTPlayerData;
import me.msicraft.ctplayerdata.PlayerData.PlayerData;
import me.msicraft.ctplayerdata.PlayerData.aCommon.TagData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionInventory implements InventoryHolder {

    public enum Type {
        MAIN, REWARD
    }

    private final Player player;;
    private final Inventory inventory;
    private final CTCollection plugin;

    private final Map<Material, Pair<Boolean, Integer>> collectionInfoMap = new HashMap<>(); //재료, <수집여부, 갯수>
    private int completeCount = 0;

    private int pageCount = 0;

    private final Map<Integer, Boolean> rewardCheckMap = new HashMap<>();
    private int rewardPageCount = 0;

    public CollectionInventory(CTCollection plugin, Player player) {
        this.player = player;
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 54, Component.text(""));
    }

    public void resetInfo() {
        collectionInfoMap.clear();
        CollectionManager collectionManager = plugin.getCollectionManager();

        List<Material> registeredMaterials = collectionManager.getCollectionMaterials();
        for (Material material : registeredMaterials) {
            setCollectionInfo(material, new Pair<>(false, 0));
        }

        rewardCheckMap.clear();

        pageCount = 0;
        rewardPageCount = 0;
        completeCount = 0;
    }

    public void updateCollection() {
        completeCount = 0;
        CollectionManager collectionManager = plugin.getCollectionManager();
        List<Material> materials = collectionManager.getCollectionMaterials();
        for (Material material : materials) {
            Collection collection = collectionManager.getCollection(material);
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
            tagData = new TagData("CTCollection", "");
        }
        tagData.setValue(getCollectionCountEncodeData());

        playerData.setTagData("CollectionInfo", tagData);
    }

    public void updateRewardInfo() {
        PlayerData playerData = CTPlayerData.getPlugin().getPlayerDataManager().getPlayerData(player);
        TagData tagData = playerData.getTagData("RewardInfo");
        if (tagData == null) {
            tagData = new TagData("CTCollection", "");
        }
        tagData.setValue(getRewardInfoEncodeData());

        playerData.setTagData("RewardInfo", tagData);
    }

    private String getRewardInfoEncodeData() {
        String s = "";
        for (int i : rewardCheckMap.keySet()) {
            boolean b = rewardCheckMap.get(i);
            if (s.isEmpty()) {
                s = i + "=" + b;
            } else {
                s = s + ":" + i + "=" + b;
            }
        }
        return Base64Util.byteArrayToString(s.getBytes());
    }

    private String getCollectionCountEncodeData() {
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

    public void open(Player player, Type type) {
        player.openInventory(inventory);
        inventory.clear();

        switch (type) {
            case MAIN -> setUpMain();
            case REWARD -> setUpReward();
        }
        player.updateInventory();
    }

    private void setUpMain() {
        String key = "CT_Collection_Menu";
        CollectionManager collectionManager = plugin.getCollectionManager();
        ItemStack itemStack = null;
        List<Material> collectionMaterials = collectionManager.getCollectionMaterials();
        int maxSize = collectionManager.getRegisteredSize();
        int maxPageSize = maxSize / 45;
        int guiCount = 0;
        int lastCount = pageCount * 45;
        for (int i = lastCount; i < maxSize; i++) {
            Material material = collectionMaterials.get(i);
            Collection collection = collectionManager.getCollection(material);
            if (collection != null) {
                Pair<Boolean, Integer> pair = collectionInfoMap.get(material);
                boolean isComplete = pair.getV1();
                if (isComplete) {
                    itemStack = collection.getCompleteStack();
                } else {
                    itemStack = collection.getCollectionStack(pair.getV2());
                }

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
        itemStack = GuiUtil.createItemStack(plugin, Material.ARROW, ChatColor.AQUA + "다음 페이지->" , GuiUtil.EMPTY_LORE,
                -1, key, "next");
        inventory.setItem(50, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.ARROW, ChatColor.AQUA + "<-이전 페이지", GuiUtil.EMPTY_LORE,
                -1, key, "previous");
        inventory.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.PAPER,  ChatColor.AQUA + "수집 완료된 도감 수: " + completeCount, GuiUtil.EMPTY_LORE,
                -1, key, "none");
        inventory.setItem(45, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.ENCHANTED_BOOK, ChatColor.AQUA + "보상 받기", GuiUtil.EMPTY_LORE,
                -1, key, "reward");
        inventory.setItem(46, itemStack);
    }

    private void setUpReward() {
        String key = "CT_Collection_Reward";
        String key2 = "CT_Collection_Reward_Button";
        RewardManager rewardManager = plugin.getRewardManager();
        ItemStack itemStack = null;
        List<Integer> countList = rewardManager.getRewardCountList();
        int maxSize = countList.size();
        int maxPageSize = maxSize / 45;
        int guiCount = 0;
        int lastCount = rewardPageCount * 45;
        for (int i = lastCount; i < maxSize; i++) {
            int count = countList.get(i);
            if (isReceived(count)) {
                itemStack = GuiUtil.createItemStack(plugin,Material.ENCHANTED_BOOK, ChatColor.GREEN + "보상 수령 완료됨 (" + count + ")",
                        GuiUtil.EMPTY_LORE, -1, key, "none");
            } else {
                String name = ChatColor.RED + "보상 수령 (" + count + ")";
                if (completeCount >= count) {
                    name = ChatColor.GREEN + "보상 수령 (" + count + ")";
                }
                itemStack = GuiUtil.createItemStack(plugin,Material.ENCHANTED_BOOK, name, GuiUtil.EMPTY_LORE, -1, key, String.valueOf(count));
            }
            this.inventory.setItem(guiCount, itemStack);
            guiCount++;
            if (guiCount >= 45) {
                break;
            }
        }
        String pageS = "페이지: " + (pageCount + 1) + "/" + (maxPageSize + 1);
        itemStack = GuiUtil.createItemStack(plugin, Material.BOOK, pageS, GuiUtil.EMPTY_LORE, -1, key2, "page");
        inventory.setItem(49, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.ARROW, ChatColor.AQUA + "다음 페이지->", GuiUtil.EMPTY_LORE,
                -1, key2, "next");
        inventory.setItem(50, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.ARROW, ChatColor.AQUA + "<-이전 페이지", GuiUtil.EMPTY_LORE,
                -1, key2, "previous");
        inventory.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.PAPER,  ChatColor.AQUA + "수집 완료된 도감 수: " + completeCount, GuiUtil.EMPTY_LORE,
                -1, key2, "xxx");
        inventory.setItem(45, itemStack);
        itemStack = GuiUtil.createItemStack(plugin, Material.PAPER, ChatColor.AQUA + "메인", GuiUtil.EMPTY_LORE,
                -1, key2, "return");
        inventory.setItem(46, itemStack);
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

    public int getRewardPageCount() {
        return rewardPageCount;
    }

    public void setRewardPageCount(int rewardPageCount) {
        this.rewardPageCount = rewardPageCount;
    }

    public boolean isReceived(int count) {
        return rewardCheckMap.getOrDefault(count, false);
    }

    public void setRewardReceived(int count, boolean check) {
        rewardCheckMap.put(count, check);
    }

    @NotNull
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
