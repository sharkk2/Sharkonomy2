package org.sharkonomy.commands.economy.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.commands.economy.SubCommand;
import org.sharkonomy.utils.PluginData;
import org.sharkonomy.utils.PluginData.PlayerData;

import java.util.*;

public class leaderboard implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return;
        }
        Player player = (Player) sender;
        String currency = Sharkonomy.getInstance().getConfig().getString("currency.currency");
        String currency_n = Sharkonomy.getInstance().getConfig().getString("currency.currency_name");
        PluginData database = Sharkonomy.getInstance().getDatabase();
        Set<Map.Entry<UUID, PlayerData>> entries = database.getData().entrySet();
        List<Map.Entry<UUID, PlayerData>> players = new ArrayList<>(entries);

        double totalBalance = 0;
        for (Map.Entry<UUID, PlayerData> entry : players) {
            totalBalance += entry.getValue().getBalance();
        }

        players.sort(Comparator.comparingDouble(e -> e.getValue().getBalance()));
        Collections.reverse(players);
        int limit = Math.min(10, players.size());
        if (limit == 0) {
            player.sendMessage(ChatColor.RED + "No players were found");
        }
        player.sendMessage("§b§l== Server Leaderboard (Balance) =====\n§dTotal server " + currency_n + ": §a" + totalBalance + " " + currency);
        for (int i = 0; i < limit; i++) {
            Map.Entry<UUID, PlayerData> entry = players.get(i);
            UUID uuid = entry.getKey();
            PlayerData playerdata = entry.getValue();
            OfflinePlayer bPlayer = Bukkit.getOfflinePlayer(uuid);
            String rcolour;
            int rank = i+1;
            switch (rank) {
                case 1: rcolour = "§e§l";
                case 2: rcolour = "§f§l";
                case 3: rcolour = "§6§l";
                default: rcolour = "§7";
            }
            player.sendMessage(rcolour + rank + ". §f" + bPlayer.getName() + " §7- §a" + playerdata.balance + " " + currency);
        }

    }
}
