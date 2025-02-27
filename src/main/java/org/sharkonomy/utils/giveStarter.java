package org.sharkonomy.utils;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.sharkonomy.Sharkonomy;

import java.util.UUID;

public class giveStarter {
    public static void give_starter(UUID playerUUID, Sharkonomy plugin) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) { return; }
        int starter = plugin.getConfig().getInt("misc.start_money");
        String currency = plugin.getConfig().getString("currency.currency");
        String currencyName = plugin.getConfig().getString("currency.currency_name");
        db database = plugin.getDatabase();

        if (!database.playerExists(playerUUID)) {
            database.addPlayer(playerUUID);
        }

        JsonObject playerdata = database.getPlayer(playerUUID);
        playerdata.addProperty("balance", starter);

        player.sendMessage(
                "Welcome §asharkk2!§r\n" +
                        "You got §6§l" + starter + " " + currency + "§7§o(" + currencyName + ")§f as a starter\n" +
                        "Earn more by playing daily, trading with players and selling items.\n" +
                        "\n" +
                        "----------------------------\n" +
                        "§oPlugin made by §9§lsharkkk2"
        );
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}
