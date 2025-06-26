package org.sharkonomy.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.sharkonomy.Currency;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.utils.PluginData;

public class rightClick implements Listener {
    private final Sharkonomy plugin;

    public rightClick(Sharkonomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        boolean rightClickEnabled = plugin.getConfig().getBoolean("features.rightClickDeposit");
        if (!rightClickEnabled) {
            return;
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.AIR) {
                Currency currency = new Currency(plugin);
                if (currency.isCurrency(item)) {
                    PluginData database = plugin.getDatabase();
                    currency.takeCurrency(player, 1);
                    PluginData.PlayerData playerData = database.getPlayer(player.getUniqueId());
                    playerData.setBalance(playerData.balance += 1);
                    playerData.addTransaction(new PluginData.Transaction(1, null, "DEPOSIT Action", 1));
                    database.savePlayer(player.getUniqueId(), playerData);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
            }
        }
    }

}
