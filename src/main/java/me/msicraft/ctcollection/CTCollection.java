package me.msicraft.ctcollection;

import me.msicraft.ctcollection.Command.MainCommand;
import me.msicraft.ctcollection.ItemCollection.Event.CollectionMenuEvent;
import me.msicraft.ctcollection.ItemCollection.Event.PlayerRelatedEvent;
import me.msicraft.ctcollection.ItemCollection.Manager.CollectionManager;
import me.msicraft.ctcollection.Reward.DataFile.RewardDataFile;
import me.msicraft.ctcollection.Reward.Manager.RewardManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class CTCollection extends JavaPlugin {

    private static CTCollection plugin;

    public static CTCollection getPlugin() {
        return plugin;
    }

    public static final String PREFIX = ChatColor.GREEN + "[CTCollection]";

    private CollectionManager collectionManager;

    private RewardManager rewardManager;
    private RewardDataFile rewardDataFile;

    @Override
    public void onEnable() {
        plugin = this;
        createConfigFiles();

        rewardDataFile = new RewardDataFile(this);

        collectionManager = new CollectionManager(this);
        rewardManager = new RewardManager(this);

        eventRegister();
        commandRegister();
        reloadVariables();

        getServer().getConsoleSender().sendMessage(PREFIX + " 플러그인이 활성화 되었습니다");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.RED + " 플러그인이 비활성화 되었습니다");
    }

    private void eventRegister() {
        getServer().getPluginManager().registerEvents(new PlayerRelatedEvent(this), this);
        getServer().getPluginManager().registerEvents(new CollectionMenuEvent(this), this);
    }

    private void commandRegister() {
        getServer().getPluginCommand("도감").setExecutor(new MainCommand(this));
    }

    public void reloadVariables() {
        reloadConfig();
    }

    private void createConfigFiles() {
        File configf = new File(getDataFolder(), "config.yml");
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public RewardDataFile getRewardDataFile() {
        return rewardDataFile;
    }

}
