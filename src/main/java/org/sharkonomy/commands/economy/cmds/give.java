package org.sharkonomy.commands.economy.cmds;


import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.commands.economy.SubCommand;
import org.sharkonomy.utils.db;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;


public class give implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /economy give <player> <amount>");
            return;
        }

        Double amount = Double.parseDouble(args[2]);

        if (amount <= 1) {
            sender.sendMessage(ChatColor.RED + "Amount must be greater than or equal to 1!");
        }


        Player target = sender.getServer().getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }


        Sharkonomy plugin = Sharkonomy.getInstance();
        db database = plugin.getDatabase();
        db.PlayerData playerData = database.getPlayer(player.getUniqueId());
        db.PlayerData targetData = database.getPlayer(target.getUniqueId());
        String currency = Sharkonomy.getInstance().getConfig().getString("currency.currency");
        String currency_n = Sharkonomy.getInstance().getConfig().getString("currency.currency_name");

        if (playerData.balance < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have enough " + currency_n + " to send!");
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }


        playerData.setBalance(playerData.balance -= amount);
        targetData.setBalance(targetData.balance += amount);

        db.Transaction transaction1 = new db.Transaction(0, target, "TRANSFER Action", amount);
        db.Transaction transaction2 = new db.Transaction(1, player, "TRANSFER Action", amount);

        playerData.addTransaction(transaction1);
        targetData.addTransaction(transaction2);

        database.savePlayer(player.getUniqueId(), playerData);
        database.savePlayer(target.getUniqueId(), targetData);

        TextComponent thanksText = new TextComponent("thanks");
        thanksText.setColor(ChatColor.LIGHT_PURPLE);
        thanksText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Thanks " + player.getName() + "!"));

        TextComponent balanceText = new TextComponent("Click here to see your balance");
        balanceText.setColor(ChatColor.LIGHT_PURPLE);
        balanceText.setBold(true);
        balanceText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/economy balance"));

        TextComponent message = new TextComponent(ChatColor.AQUA + player.getName() + ChatColor.WHITE + " has sent you " +
                ChatColor.GOLD + ChatColor.BOLD + amount + " " + currency + ChatColor.WHITE + "! Say ");
        message.addExtra(thanksText);
        message.addExtra(new TextComponent(ChatColor.WHITE + "!\n"));
        message.addExtra(balanceText);

        target.spigot().sendMessage(message);
        target.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        TextComponent senderMessage = new TextComponent("You have sent " +
                ChatColor.GOLD + ChatColor.BOLD + amount + " " + currency + ChatColor.RESET + " to " +
                ChatColor.AQUA + target.getName() + "\n");
        senderMessage.addExtra(balanceText);
        player.spigot().sendMessage(senderMessage);

    }
}
