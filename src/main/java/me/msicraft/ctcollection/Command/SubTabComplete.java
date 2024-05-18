package me.msicraft.ctcollection.Command;

import me.msicraft.ctcollection.CTCollection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SubTabComplete implements TabCompleter {

    private final CTCollection plugin;

    public SubTabComplete(CTCollection plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("ctcollection")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("reload");
                arguments.add("reset");
                arguments.add("import-item");
                return arguments;
            }
        }
        return null;
    }

}
