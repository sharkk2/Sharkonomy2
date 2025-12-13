package org.sharkonomy.commands.economy.cmds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sharkonomy.Currency;
import org.sharkonomy.commands.economy.SubCommand;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.utils.PluginData;
import org.sharkonomy.utils.Helpers;


public class withdraw implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return;
        }

        Player player = (Player) sender;

        boolean withdrawalEnabled = Sharkonomy.getInstance().getConfig().getBoolean("features.enable_withdraw");
        if (!withdrawalEnabled) {
            sender.sendMessage(ChatColor.RED + "Withdrawing is disabled by server admins");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
            return;
        }

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
        String currency_n = Sharkonomy.getInstance().getConfig().getString("currency.currency_name");
        String currency_item = Sharkonomy.getInstance().getConfig().getString("currency.currency_item");

        if (playerData.balance < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have " + amount + " " + currency + " to withdraw!");
            return;
        }

        String currencyItemName;
        Material mat = Material.getMaterial(currency_item.toUpperCase());
        if (mat != null || mat.isItem()) {

            currencyItemName = Helpers.formatMatName(mat);
        } else {
            Bukkit.getLogger().warning("Currency item: " + currency_item + " is invalid!");
            sender.sendMessage(ChatColor.RED + "Currency item: " + currency_item + " is invalid! please check the 'config.yml' file of the plugin");
            return;
        }

        playerData.addTransaction(new PluginData.Transaction(0, null, "WITHDRAW Action", amount));

        Sharkonomy plugin = Sharkonomy.getInstance();
        Currency currencyManager = new Currency(plugin);
        currencyManager.giveCurrency(player.getUniqueId(), amount);
        playerData.setBalance(playerData.balance -= amount);

        database.savePlayer(player.getUniqueId(), playerData);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        boolean rightClickDeposit = plugin.getConfig().getBoolean("features.rightClickDeposit");

        if (rightClickDeposit) {
            player.sendMessage("You have successfully withdrawn §6§l" + amount + " " + currency + "§r.\n" +
                    "----------------------------------\n" +
                    "Withdrawn §7(" + currency_n + ") §fare in the form of a §b" + currencyItemName + ".\n" +
                    "§8(Right click while holding to deposit back)");
        } else {
            player.sendMessage("You have successfully withdrawn §6§l" + amount + " " + currency + "§r.\n" +
                    "----------------------------------\n" +
                    "Withdrawn §7(" + currency_n + ") §fare in the form of a §b" + currencyItemName + ".");
        }



    }
}
