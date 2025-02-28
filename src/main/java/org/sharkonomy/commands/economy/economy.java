package org.sharkonomy.commands.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sharkonomy.commands.economy.cmds.balance;
import org.jetbrains.annotations.NotNull;
import org.sharkonomy.commands.economy.cmds.give;

import java.util.HashMap;
import java.util.Map;

public class economy implements CommandExecutor {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public economy() {
        subCommands.put("balance", new balance());
        subCommands.put("give", new give());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /economy <subcommand>");
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            subCommand.execute(sender, args);
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /economy <subcommand>");
        }
        return true;
    }
}