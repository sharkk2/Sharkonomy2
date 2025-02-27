package org.sharkonomy.commands.economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sharkonomy.commands.economy.cmds.balance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class economy implements CommandExecutor {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public economy() {
        subCommands.put("balance", new balance());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /economy <subcommand>");
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            subCommand.execute(sender, args);
        } else {
            sender.sendMessage("§cUnknown subcommand. Use /economy <balance/give>");
        }
        return true;
    }
}