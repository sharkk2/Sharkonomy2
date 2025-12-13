package org.sharkonomy.commands.economy.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.commands.economy.SubCommand;
import org.sharkonomy.utils.Helpers;
import org.sharkonomy.utils.PluginData;
import org.sharkonomy.utils.PluginData.*;
import java.io.IOException;
// UNTESTED AT ALL!!!
public class sell implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return;
        }
        Player player = (Player) sender;

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /economy sell <quantity> <price>");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You must be holding something in your main hand to sell");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
            return;
        }

        int quanitity;
        double price;
        try {
            quanitity = Integer.parseInt(args[1]);
            price = Double.parseDouble(args[2]);
            if ((quanitity <= 0 || quanitity > item.getMaxStackSize()) || price < 0) {
                throw new NumberFormatException("nigga");
            }

        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Quantity must be between (1 to " + item.getMaxStackSize() + ") and price must be bigger than 0");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
            return;
        }

        String serial;
        try {
            serial = Helpers.serializeItem(item);
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
            sender.sendMessage(ChatColor.RED + "An error occurred while selling");
            return;
        }

        ShopItem shopitem = new ShopItem(quanitity, price, serial);
        PluginData database = Sharkonomy.getInstance().getDatabase();
        PlayerData playerData = database.getPlayer(player.getUniqueId());
        playerData.addShopItem(shopitem);
        int quantityOld = quanitity;
        for (ItemStack invitem : player.getInventory().getContents()) {
            if (invitem == null) continue;
            if (!invitem.isSimilar(item)) continue;
            int remove = Math.min(invitem.getAmount(), quanitity);
            invitem.setAmount(item.getAmount() - remove);
            quanitity -= remove;
            if (quanitity <= 0) break;
        }
        database.savePlayer(player.getUniqueId(), playerData);
        String currency = Sharkonomy.getInstance().getConfig().getString("currency.currency");
        sender.sendMessage("Added §b" + quantityOld + "§l" + item.displayName() + "§rto your shop for §6" + price + currency.toString() + "§reach\n" +
                "---------------------------\n" +
                "§7You can remove it from  your shop by buying it yourself");
        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 1.0f, 1.0f);
    }
}
