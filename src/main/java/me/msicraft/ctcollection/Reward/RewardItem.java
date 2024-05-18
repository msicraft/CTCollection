package me.msicraft.ctcollection.Reward;

import org.bukkit.inventory.ItemStack;

public class RewardItem {

    private final RewardItemType rewardItemType;
    private final int minAmount;
    private final int maxAmount;
    private final ItemStack itemStack;

    public RewardItem(RewardItemType rewardItemType, int minAmount, int maxAmount, ItemStack itemStack) {
        this.rewardItemType = rewardItemType;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        if (rewardItemType == RewardItemType.MONEY) {
            this.itemStack = null;
        } else {
            this.itemStack = itemStack;
        }
    }

    public RewardItemType getRewardItemType() {
        return rewardItemType;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

}
