package me.msicraft.ctcollection.Event;

import me.msicraft.ctcollection.CTCollection;
import me.msicraft.ctcollection.Manager.CollectionManager;
import me.msicraft.ctcollection.Menu.CollectionInventory;
import me.msicraft.ctcollection.aCommon.Collection;
import me.msicraft.ctplayerdata.CTPlayerData;
import me.msicraft.ctplayerdata.PlayerData.PlayerData;
import me.msicraft.ctplayerdata.PlayerData.aCommon.TagData;
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
                    int maxPage = collectionManager.getCollectionIds().size() / 45;
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
            } else if (dataContainer.has(new NamespacedKey(plugin, "CT_Collection_Stack_Id"), PersistentDataType.STRING)) {
                String data = dataContainer.get(new NamespacedKey(plugin, "CT_Collection_Stack_Id"), PersistentDataType.STRING);
                if (data != null) {
                    int id = Integer.parseInt(data);
                    Collection collection = collectionManager.getCollection(id);
                    if (collection != null) {
                    }
                }
            }
        }
    }

}
