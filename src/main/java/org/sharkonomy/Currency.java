package org.sharkonomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class Currency {
    private final Sharkonomy plugin;

    public Currency(Sharkonomy plugin) {
        this.plugin = plugin;
    }

    public static final NamespacedKey CURRENCY_KEY = new NamespacedKey("sharkonomy", "sharcoin");

    public void giveCurrency(UUID playerUUID, int amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || amount <= 0) return;

        String currencyItem = plugin.getConfig().getString("currency.currency_item");
        String currencyName = plugin.getConfig().getString("currency.currency_item_name");
        boolean currencyInvincible= plugin.getConfig().getBoolean("currency.invincible_item");

        Material mat = Material.getMaterial(currencyItem.toUpperCase());
        if (mat == null || !mat.isItem()) {
            Bukkit.getLogger().warning("Currency item: " + currencyItem + " is invalid!");
            return;
        }

        ItemStack currency = new ItemStack(mat, amount);
        ItemMeta meta = currency.getItemMeta();
        meta.setDisplayName(currencyName);
        if (currencyInvincible) {
            meta.setFireResistant(true);
            meta.setUnbreakable(true);
        }
        meta.setRarity(ItemRarity.RARE);
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(CURRENCY_KEY, PersistentDataType.BYTE, (byte) 1);
        currency.setItemMeta(meta);

        player.getInventory().addItem(currency);
    }

    public int countCurrency(Player player) {
        int total = 0;
        for (ItemStack item : player.getInventory()) {
            if (isCurrency(item)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public boolean takeCurrency(Player player, int amount) {
        if (amount <= 0) return false;
        int rem = amount;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (!isCurrency(item)) continue;
            int stackAmount = item.getAmount();
            if (stackAmount <= rem) {
                player.getInventory().setItem(i, null);
                rem -= stackAmount;
            } else {
                item.setAmount(stackAmount - rem);
                rem = 0;
            }
            if (rem == 0) break;
        }
        return rem == 0;
    }

    public boolean isCurrency(ItemStack item) {
        String currencyItem = plugin.getConfig().getString("currency.currency_item");

        Material mat = Material.getMaterial(currencyItem.toUpperCase());
        if (mat == null || !mat.isItem()) {
            Bukkit.getLogger().warning("Currency item: " + currencyItem + " is invalid!");
            return false;
        }

        if (item == null || item.getType() != mat || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(CURRENCY_KEY, PersistentDataType.BYTE);
    }
}
