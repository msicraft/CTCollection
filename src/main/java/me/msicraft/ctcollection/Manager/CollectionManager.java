package me.msicraft.ctcollection.Manager;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.Menu.CollectionInventory;
import me.msicraft.ctcollection.aCommon.Collection;
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

    private final Map<Integer, Collection> registeredCollectionsMap = new HashMap<>();

    private final Map<UUID, Pair<CollectionInventory, Inventory>> cachedInventoryMap = new HashMap<>();

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
                    Collection collection = new Collection(count, material, amount);
                    registeredCollectionsMap.put(count, collection);
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
    }

    public Set<Integer> getCollectionIds() {
        return registeredCollectionsMap.keySet();
    }

    public Set<Material> getCollectionMaterials() {
        Set<Material> materials = new HashSet<>();
        for (Collection collection : registeredCollectionsMap.values()) {
            materials.add(collection.getMaterial());
        }
        return materials;
    }

    public boolean hasCollection(int id) {
        return registeredCollectionsMap.containsKey(id);
    }

    public Collection getCollection(int id) {
        return registeredCollectionsMap.getOrDefault(id, null);
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


}
