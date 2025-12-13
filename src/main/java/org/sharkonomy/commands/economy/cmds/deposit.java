package org.sharkonomy.commands.economy.cmds;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sharkonomy.Currency;
import org.sharkonomy.commands.economy.SubCommand;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.utils.PluginData;

public class deposit implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return;
        }
        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /economy withdraw <amount>");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
            return;
        }

        if (amount <= 1) {
            sender.sendMessage(ChatColor.RED + "Amount must be greater than or equal to 1!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
            return;
        }

        PluginData database = Sharkonomy.getInstance().getDatabase();
        PluginData.PlayerData playerData = database.getPlayer(player.getUniqueId());
        String currency = Sharkonomy.getInstance().getConfig().getString("currency.currency");

        Currency currencyManager = new Currency(Sharkonomy.getInstance());

        int heldCurrency = currencyManager.countCurrency(player);
        if (heldCurrency < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have " + amount + " " + currency + " to deposit");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
            return;
        }

        currencyManager.takeCurrency(player, amount);
        playerData.setBalance(playerData.balance += amount);
        playerData.addTransaction(new PluginData.Transaction(1, null, "DEPOSIT Action", amount));
        database.savePlayer(player.getUniqueId(), playerData);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        player.sendMessage("You have successfully deposited §6§l" + amount + " " + currency + "§r.\n" +
                "----------------------------------\n" +
                "Check your balance with /economy balance");

    }
}
