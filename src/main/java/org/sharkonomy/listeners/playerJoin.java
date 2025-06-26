package org.sharkonomy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.utils.PluginData;
import org.sharkonomy.utils.giveStarter;

public class playerJoin implements Listener {

    private final Sharkonomy plugin;

    public playerJoin(Sharkonomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!giveStarter.give_starter(player.getUniqueId(), plugin)) {
            PluginData plugindb = new PluginData(plugin);
            plugindb.giveDaily(player.getUniqueId());

        }
    }
}
