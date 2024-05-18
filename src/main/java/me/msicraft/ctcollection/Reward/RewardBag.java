package me.msicraft.ctcollection.Reward;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.Reward.Manager.RewardManager;
import me.msicraft.ctcore.aCommon.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RewardBag {

    private final int minAmount;
    private final int maxAmount;

    private final List<Pair<RewardItem, Double>> weightItemsList;

    public RewardBag(String internalName, int minAmount, int maxAmount, List<String> itemsInternalNames, RewardManager manager) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;

        int size = itemsInternalNames.size();

        weightItemsList = new ArrayList<>(size);

        int totalWeight = 0;
        List<Pair<RewardItem, Integer>> tempList = new ArrayList<>(size);

        for (String itemInternalName : itemsInternalNames) {
            String[] a = itemInternalName.split(":");
            try {
                RewardItem rewardItem = manager.getRewardItem(a[0]);
                if (rewardItem != null) {
                    int weight = Integer.parseInt(a[1]);

                    tempList.add(new Pair<>(rewardItem, weight));
                    totalWeight = totalWeight + weight;
                } else {
                    Bukkit.getConsoleSender().sendMessage(CTCollection.PREFIX + ChatColor.RED + " Invalid Item Internal Name: " + internalName + " (" +itemInternalName + ")");
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        for (Pair<RewardItem, Integer> pair : tempList) {
            double value = (double) pair.getV2() / totalWeight;
            weightItemsList.add(new Pair<>(pair.getV1(), value));
        }

        weightItemsList.sort(Comparator.comparingDouble(Pair::getV2));
    }

    public RewardItem getRandomRewardItem() {
        double pivot = Math.random();
        double weight = 0;
        for (Pair<RewardItem, Double> pair : weightItemsList) {
            weight = weight + pair.getV2();
            if (pivot <= weight) {
                return pair.getV1();
            }
        }
        return null;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

}
