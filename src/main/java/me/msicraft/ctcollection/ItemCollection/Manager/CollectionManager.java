package me.msicraft.ctcollection.ItemCollection.Manager;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.ItemCollection.Menu.CollectionInventory;
import me.msicraft.ctcollection.ItemCollection.Collection;
import me.msicraft.ctcore.aCommon.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.regex.PatternSyntaxException;

public class CollectionManager {

    private final CTCollection plugin;

    private final Map<Material, Collection> registeredCollectionsMap = new LinkedHashMap<>();

    private final Map<UUID, Pair<CollectionInventory, Inventory>> cachedInventoryMap = new HashMap<>();

    private int registeredSize = 0;

    public CollectionManager(CTCollection plugin) {
        this.plugin = plugin;

        reloadVariables();
    }

    public void reloadVariables() {
        registeredCollectionsMap.clear();

        int count = 0;
        List<String> collectionFormatList = plugin.getConfig().getStringList("Collections");
        for (String format : collectionFormatList) {
            try {
                String[] a = format.split(":");
                Material material = Material.getMaterial(a[0].toUpperCase());
                if (material != null) {
                    int amount = Integer.parseInt(a[1]);
                    Collection collection = new Collection(material, amount);
                    registeredCollectionsMap.put(material, collection);
                    count++;
                } else {
                    Bukkit.getConsoleSender().sendMessage(CTCollection.PREFIX + ChatColor.RED + " Invalid Material Name: " + format);
                }
            } catch (PatternSyntaxException | NumberFormatException e) {
                Bukkit.getConsoleSender().sendMessage(CTCollection.PREFIX + ChatColor.RED + " Invalid Format: " + format);
            }
        }
        Bukkit.getConsoleSender().sendMessage(CTCollection.PREFIX + ChatColor.GREEN + " Registered "
                + ChatColor.GOLD + count
                + ChatColor.GREEN + " Collections");
        registeredSize = count;
    }

    public List<Material> getCollectionMaterials() {
        return List.copyOf(registeredCollectionsMap.keySet());
    }

    public boolean hasCollection(Material material) {
        return registeredCollectionsMap.containsKey(material);
    }

    public Collection getCollection(Material material) {
        return registeredCollectionsMap.getOrDefault(material, null);
    }

    public void setCachedInventory(Player player, Pair<CollectionInventory, Inventory> pair) {
        setCachedInventory(player.getUniqueId(), pair);
    }

    public void setCachedInventory(UUID uuid, Pair<CollectionInventory, Inventory> pair) {
        cachedInventoryMap.put(uuid, pair);
    }

    public Pair<CollectionInventory, Inventory> getCachedInventory(Player player) {
        return getCachedInventory(player.getUniqueId());
    }

    public Pair<CollectionInventory, Inventory> getCachedInventory(UUID uuid) {
        return cachedInventoryMap.getOrDefault(uuid, null);
    }

    public void removeCachedInventory(Player player) {
        removeCachedInventory(player.getUniqueId());
    }

    public void removeCachedInventory(UUID uuid) {
        cachedInventoryMap.remove(uuid);
    }

    public int getRegisteredSize() {
        return registeredSize;
    }

}
