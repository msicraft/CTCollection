package me.msicraft.ctcollection.Reward.Manager;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.Reward.DataFile.RewardDataFile;
import me.msicraft.ctcollection.Reward.Reward;
import me.msicraft.ctcollection.Reward.RewardBag;
import me.msicraft.ctcollection.Reward.RewardItem;
import me.msicraft.ctcollection.Reward.RewardItemType;
import me.msicraft.ctcore.aCommon.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RewardManager {

    private final CTCollection plugin;

    private final Map<Integer, Reward> rewardMap = new LinkedHashMap<>();
    private final Map<String, RewardItem> rewardItemsMap = new HashMap<>();
    private final Map<String, RewardBag> rewardBagsMap = new HashMap<>();

    public RewardManager(CTCollection plugin) {
        this.plugin = plugin;
        reloadVariables();
    }

    public void reloadVariables() {
        reloadRewards();
        reloadRewardItems();
        reloadRewardBags();
    }

    private void reloadRewards() {
        rewardMap.clear();

        RewardDataFile rewardDataFile = plugin.getRewardDataFile();

        ConfigurationSection rewardSection = rewardDataFile.getConfig().getConfigurationSection("Reward");
        if (rewardSection != null) {
            Set<String> sets = rewardSection.getKeys(false);
            List<Pair<Integer, List<String>>> pairs = new ArrayList<>();
            for (String countS : sets) {
                int count = Integer.parseInt(countS);
                List<String> list = rewardDataFile.getConfig().getStringList("Reward." + countS + ".List");

                pairs.add(new Pair<>(count, list));
            }

            pairs.sort(Comparator.comparingInt(Pair::getV1));
            for (Pair<Integer, List<String>> pair : pairs) {
                rewardMap.put(pair.getV1(), new Reward(pair.getV1(), pair.getV2()));
            }
        }
    }

    private void reloadRewardItems() {
        rewardItemsMap.clear();
        RewardDataFile rewardDataFile = plugin.getRewardDataFile();

        ConfigurationSection itemSection = rewardDataFile.getConfig().getConfigurationSection("Items");
        if (itemSection != null) {
            Set<String> sets = itemSection.getKeys(false);
            for (String internalName : sets) {
                String path = "Items." + internalName;
                String typeS = rewardDataFile.getConfig().getString(path + ".Type");
                if (typeS == null) {
                    Bukkit.getConsoleSender().sendMessage(Component.text(CTCollection.PREFIX + ChatColor.RED + " Type value does not exist: " + internalName));
                    continue;
                }
                try {
                    RewardItemType itemType = RewardItemType.valueOf(typeS.toUpperCase());
                    int minAmount = rewardDataFile.getConfig().getInt(path + ".MinAmount");
                    int maxAmount = rewardDataFile.getConfig().getInt(path + ".MaxAmount");
                    ItemStack itemStack = null;
                    if (itemType == RewardItemType.ITEMSTACK) {
                        itemStack = rewardDataFile.getConfig().getItemStack(path + ".ItemStack");
                    }
                    rewardItemsMap.put(internalName, new RewardItem(itemType, minAmount, maxAmount, itemStack));
                } catch (IllegalArgumentException e) {
                    Bukkit.getConsoleSender().sendMessage(Component.text(CTCollection.PREFIX + ChatColor.RED + " Invalid Type value: " + internalName));
                }
            }
        }
    }

    private void reloadRewardBags() {
        rewardBagsMap.clear();
        RewardDataFile rewardDataFile = plugin.getRewardDataFile();

        ConfigurationSection bagSection = rewardDataFile.getConfig().getConfigurationSection("Bags");
        if (bagSection != null) {
            Set<String> sets = bagSection.getKeys(false);
            for (String internalName : sets) {
                String path = "Bags." + internalName;
                int minAmount = rewardDataFile.getConfig().getInt(path + ".MinAmount");
                int maxAmount = rewardDataFile.getConfig().getInt(path + ".MaxAmount");
                List<String> list = rewardDataFile.getConfig().getStringList(path + ".List");
                rewardBagsMap.put(internalName, new RewardBag(internalName, minAmount, maxAmount, list, this));
            }
        }
    }

    public RewardItem getRewardItem(String internalName) {
        return rewardItemsMap.get(internalName);
    }

    public RewardBag getRewardBag(String internalName) {
        return rewardBagsMap.get(internalName);
    }

    public Reward getReward(int count) {
        return rewardMap.get(count);
    }

    public List<Integer> getRewardCountList() {
        return List.copyOf(rewardMap.keySet());
    }

    public int getRewardSize() {
        return rewardMap.size();
    }

}
