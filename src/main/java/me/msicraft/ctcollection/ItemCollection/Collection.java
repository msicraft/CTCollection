package me.msicraft.ctcollection.ItemCollection;

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

    private final Material material;
    private final int amount;

    public Collection(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getCompleteStack() {
        ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text(ChatColor.GREEN + "" + ChatColor.BOLD + "수집 완료"));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack getCollectionStack(int count) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.lore(List.of(Component.text(ChatColor.YELLOW + "좌 클릭: 1개 수집"),
                Component.text(ChatColor.YELLOW + "우 클릭: 보유한 최대치 수집"),
                Component.text(""),
                Component.text(ChatColor.GREEN + "수집 상태: " + ChatColor.BOLD + count + "/" + amount)));

        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(CTCollection.getPlugin(), "CT_Collection_Material"), PersistentDataType.STRING, material.name());
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
