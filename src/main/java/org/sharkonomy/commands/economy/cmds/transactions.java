package org.sharkonomy.commands.economy.cmds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sharkonomy.commands.economy.SubCommand;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.utils.PluginData;
import org.sharkonomy.utils.PluginData.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class transactions implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return;
        }

        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid page number!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
                return;
            }
        }

        PluginData database = Sharkonomy.getInstance().getDatabase();
        PlayerData playerData = database.getPlayer(player.getUniqueId());
        Map<Integer, Transaction> transactions = playerData.transactions;

        if (transactions.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "No transactions found.");
            return;
        }

        int totalPages = (int) Math.ceil(transactions.size() / (double) 5);
        if (page < 1 || page > totalPages) {
            player.sendMessage(ChatColor.RED + "Invalid page! total pages: " + totalPages);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Transactions (Page " + page + "/" + totalPages + ")");
        player.sendMessage("=============================");
        List<Transaction> sorted = new ArrayList<>(transactions.values());
        sorted.sort(Comparator.comparingLong(t -> -t.date));
        int start = (page - 1) * 5;
        int end = Math.min(start + 5, sorted.size());

        for (int i = start; i < end; i++) {
            Transaction t = sorted.get(i);
            String type = t.type == 0 ? ChatColor.RED + "-" : ChatColor.GREEN + "+";
            String entry = ChatColor.GRAY + "[" + ChatColor.YELLOW + new SimpleDateFormat("MMM-dd hh:mm a").format(new Date(t.date)) + ChatColor.GRAY + "] "
                    + type + t.amount + ChatColor.GRAY + " from "
                    + ChatColor.AQUA + t.transactorName + ChatColor.GRAY + " (" + t.description + ")";
            player.sendMessage(entry); // honestly using § was better
        }
    }

}
