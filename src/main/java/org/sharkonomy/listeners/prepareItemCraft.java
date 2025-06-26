package org.sharkonomy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.sharkonomy.Sharkonomy;

import static org.sharkonomy.Currency.CURRENCY_KEY;

public class prepareItemCraft implements Listener {
    private final Sharkonomy plugin;

    public prepareItemCraft(Sharkonomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || !item.hasItemMeta()) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta.getPersistentDataContainer().has(CURRENCY_KEY, PersistentDataType.BYTE)) {
                event.getInventory().setResult(null);
                break;
            }
        }
    }


}
