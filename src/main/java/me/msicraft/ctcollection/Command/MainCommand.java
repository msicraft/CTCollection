package me.msicraft.ctcollection.Command;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.ItemCollection.Menu.CollectionInventory;
import me.msicraft.ctcore.aCommon.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    private final CTCollection plugin;

    public MainCommand(CTCollection plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equals("도감")) {
            if (sender instanceof Player player) {
                Pair<CollectionInventory, Inventory> pair = plugin.getCollectionManager().getCachedInventory(player);
                CollectionInventory collectionInventory = pair.getV1();
                collectionInventory.open(player);
                return true;
            }
        }
        return false;
    }

}
