package org.sharkonomy.commands.economy.cmds;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sharkonomy.commands.economy.SubCommand;

public class balance implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can check their balance!");
            return;
        }
    }
}
