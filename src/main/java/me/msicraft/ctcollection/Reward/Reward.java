package me.msicraft.ctcollection.Reward;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.Reward.Manager.RewardManager;
import me.msicraft.ctcore.Utils.EntityUtil;
import me.msicraft.ctcore.Utils.MathUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class Reward {

    private final int collectionCount;
    private final List<String> rewardList;

    public Reward(int collectionCount, List<String> rewardList) {
        this.collectionCount = collectionCount;
        this.rewardList = rewardList;
    }

    public void giveRewardToPlayer(Player player) {
        RewardManager rewardManager = CTCollection.getPlugin().getRewardManager();
        for (String bagInternalName : rewardList) {
            RewardBag rewardBag = rewardManager.getRewardBag(bagInternalName);
            if (rewardBag != null) {
                int amount = MathUtil.getRangeRandomInt(rewardBag.getMaxAmount(), rewardBag.getMinAmount());
                for (int i = 0 ; i<amount; i++) {
                    RewardItem rewardItem = rewardBag.getRandomRewardItem();
                    RewardItemType itemType = rewardItem.getRewardItemType();
                    switch (itemType) {
                        case MONEY -> {
                            CTCollection.getEcon().depositPlayer(player, MathUtil.getRangeRandomInt(rewardItem.getMaxAmount(), rewardItem.getMinAmount()));
                        }
                        case ITEMSTACK -> {
                            int emptySlot = EntityUtil.getPlayerEmptySlot(player);
                            if (emptySlot != -1) {
                                player.getInventory().setItem(emptySlot, rewardItem.getItemStack());
                            } else {
                                player.getWorld().dropItemNaturally(player.getLocation(), rewardItem.getItemStack());
                            }
                        }
                    }
                }
            }
        }
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public List<String> getRewardList() {
        return rewardList;
    }

}
