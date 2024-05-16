package me.msicraft.ctcollection.ItemCollection.Event;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.ItemCollection.Manager.CollectionManager;
import me.msicraft.ctcollection.ItemCollection.Menu.CollectionInventory;
import me.msicraft.ctcollection.ItemCollection.Collection;
import me.msicraft.ctcore.aCommon.Pair;
import me.msicraft.ctplayerdata.CTPlayerData;
import me.msicraft.ctplayerdata.PlayerData.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CollectionMenuEvent implements Listener {

    private final CTCollection plugin;

    public CollectionMenuEvent(CTCollection plugin) {
        this.plugin = plugin;
    }

    private int getMaterialSlot(Player player, Material material) {
        int i = -1;
        int max = 36;
        for (int j = 0; j < max; j++) {
            ItemStack checkStack = player.getInventory().getItem(j);
            if (checkStack != null && checkStack.getType() == material) {
                ItemMeta checkMeta = checkStack.getItemMeta();
                if (checkMeta.hasDisplayName() || checkMeta.hasLore()) {
                    continue;
                }
                i = j;
                break;
            }
        }
        return i;
    }

    @EventHandler
    public void clickCollectionMenu(InventoryClickEvent e) {
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof CollectionInventory collectionInventory) {
            ClickType type = e.getClick();
            if (type == ClickType.NUMBER_KEY || type == ClickType.SWAP_OFFHAND
                    || type == ClickType.SHIFT_LEFT || type == ClickType.SHIFT_RIGHT) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null) {
                return;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return;
            }
            CollectionManager collectionManager = plugin.getCollectionManager();
            PlayerData playerData = CTPlayerData.getPlugin().getPlayerDataManager().getPlayerData(player);
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(new NamespacedKey(plugin, "CT_Collection_Menu"), PersistentDataType.STRING)) {
                String data = dataContainer.get(new NamespacedKey(plugin, "CT_Collection_Menu"), PersistentDataType.STRING);
                if (data != null) {
                    int maxPage = collectionManager.getRegisteredSize() / 45;
                    int current = collectionInventory.getPageCount();
                    switch (data) {
                        case "next" -> {
                            int next = current + 1;
                            if (next > maxPage) {
                                next = 0;
                            }
                            collectionInventory.setPageCount(next);
                            collectionInventory.open(player);
                        }
                        case "previous" -> {
                            int previous = current - 1;
                            if (previous < 0) {
                                previous = maxPage;
                            }
                            collectionInventory.setPageCount(previous);
                            collectionInventory.open(player);
                        }
                    }
                }
            } else if (dataContainer.has(new NamespacedKey(plugin, "CT_Collection_Material"), PersistentDataType.STRING)) {
                String data = dataContainer.get(new NamespacedKey(plugin, "CT_Collection_Material"), PersistentDataType.STRING);
                if (data != null) {
                    Material material = Material.getMaterial(data.toUpperCase());
                    if (material != null) {
                        int materialSlot = getMaterialSlot(player, material);
                        if (materialSlot == -1) {
                            player.sendMessage(Component.text(ChatColor.RED + "해당 아이템이 인벤토리에 존재하지 않습니다."));
                            return;
                        }
                        int required = 1;
                        Collection collection = collectionManager.getCollection(material);
                        Pair<Boolean, Integer> pair = collectionInventory.getCollectionInfo(material);
                        if (e.isLeftClick()) {
                            ItemStack item = player.getInventory().getItem(materialSlot);
                            item.setAmount(item.getAmount() - 1);

                            player.sendMessage(Component.text(ChatColor.GREEN + "해당 아이템이 1 개 등록되었습니다"));
                        } else if (e.isRightClick()) {
                            int left = collection.getAmount() - pair.getV2();
                            int max = 36;
                            int count = 0;
                            for (int j = 0; j < max; j++) {
                                ItemStack checkStack = player.getInventory().getItem(j);
                                if (checkStack != null && checkStack.getType() == material) {
                                    ItemMeta checkMeta = checkStack.getItemMeta();
                                    if (checkMeta.hasDisplayName() || checkMeta.hasLore()) {
                                        continue;
                                    }
                                    int size = checkStack.getAmount();
                                    checkStack.setAmount(0);
                                    count = count + size;
                                    if (count >= left) {
                                        int cal = count - left;
                                        count = count - cal;
                                        ItemStack clone = new ItemStack(material);
                                        for (int i = 0; i < cal; i++) {
                                            player.getInventory().addItem(clone);
                                        }
                                        break;
                                    }
                                }
                            }
                            required = count;
                            player.sendMessage(Component.text(ChatColor.GREEN + "해당 아이템이 " + count + " 개 등록되었습니다"));
                        }

                        int next = pair.getV2() + required;
                        if (next >= collection.getAmount()) {
                            next = collection.getAmount();
                            pair.setV1(true);
                        }
                        pair.setV2(next);

                        collectionInventory.setCollectionInfo(material, pair);
                        collectionInventory.updateCollection();

                        collectionInventory.open(player);
                    }
                }
            }
        }
    }

}
