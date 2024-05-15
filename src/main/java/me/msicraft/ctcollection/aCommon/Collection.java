package me.msicraft.ctcollection.aCommon;

import me.msicraft.ctcollection.CTCollection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Collection {

    private final int id;

    private final Material material;
    private final int amount;

    public Collection(int id, Material material, int amount) {
        this.id = id;
        this.material = material;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getCollectionStack(boolean isComplete) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (isComplete) {
            itemMeta.lore(List.of(Component.text(ChatColor.GREEN + "수집 상태: " + ChatColor.BOLD + ChatColor.GOLD + "O")));
        } else {
            itemMeta.lore(List.of(Component.text(ChatColor.GREEN + "수집 상태: " + ChatColor.BOLD + ChatColor.RED + "X")));
        }
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(CTCollection.getPlugin(), "CT_Collection_Menu"), PersistentDataType.STRING, "collection_stack");
        dataContainer.set(new NamespacedKey(CTCollection.getPlugin(), "CT_Collection_Stack_Id"), PersistentDataType.STRING, String.valueOf(id));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
