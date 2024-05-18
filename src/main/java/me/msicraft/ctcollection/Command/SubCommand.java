package me.msicraft.ctcollection.Command;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.ItemCollection.Menu.CollectionInventory;
import me.msicraft.ctcollection.Reward.RewardItemType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SubCommand implements CommandExecutor {

    private final CTCollection plugin;

    public SubCommand(CTCollection plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equals("ctcollection")) {
            if (args.length >= 1 && sender.isOp()) {
                String var = args[0];
                switch (var) {
                    case "reload" -> {
                        plugin.reloadVariables();
                        sender.sendMessage(Component.text(ChatColor.GREEN + "구성이 리로드 되었습니다"));
                        return true;
                    }
                    case "reset" -> {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage(Component.text(ChatColor.RED + "해당 플레이어가 존재하지 않습니다"));
                            return true;
                        }
                        if (target.getInventory().getHolder(false) instanceof CollectionInventory) {
                            target.closeInventory();
                        }
                        CollectionInventory collectionInventory = plugin.getCollectionManager().getCachedInventory(target).getV1();
                        collectionInventory.resetInfo();

                        sender.sendMessage(Component.text(ChatColor.GREEN + "해당 플레이어의 도감정보가 리셋 되었습니다"));
                        return true;
                    }
                    case "import-item" -> {
                        if (sender instanceof Player player) {
                            ItemStack handStack = player.getInventory().getItemInMainHand();
                            if (handStack.getType() == Material.AIR) {
                                player.sendMessage(Component.text(ChatColor.RED + "공기는 등록 불가능합니다"));
                                return true;
                            }
                            try {
                                String internalName = args[1];
                                int minAmount = Integer.parseInt(args[2]);
                                int maxAmount = Integer.parseInt(args[3]);
                                plugin.getRewardDataFile().getConfig().set("Items." + internalName + ".Type", RewardItemType.ITEMSTACK.name().toUpperCase());
                                plugin.getRewardDataFile().getConfig().set("Items." + internalName + ".MinAmount", minAmount);
                                plugin.getRewardDataFile().getConfig().set("Items." + internalName + ".MaxAmount", maxAmount);
                                plugin.getRewardDataFile().getConfig().set("Items." + internalName + ".ItemStack", handStack);
                                plugin.getRewardDataFile().saveConfig();
                                player.sendMessage(ChatColor.GREEN + "해당 아이템이 등록되었습니다 (/ctcollection reload)");
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                player.sendMessage(Component.text(ChatColor.RED + "/ctcollection import <internalName> <minAmount> <maxAmount>"));
                            }
                        } else {
                            sender.sendMessage(Component.text(ChatColor.RED + "플레이어만 사용가능한 명령어 입니다"));
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
