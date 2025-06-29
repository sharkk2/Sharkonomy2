package org.sharkonomy.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.sharkonomy.Sharkonomy;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PluginData {

    private final Sharkonomy plugin;
    private final File dbFile;
    private final Gson gson;
    private Map<UUID, PlayerData> database;

    public PluginData(Sharkonomy plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dbFile = new File(plugin.getDataFolder(), "sharkonomy.json");
        loadDB();
    }

    private void loadDB() {
        if (!dbFile.exists()) {
            database = new HashMap<>();
            saveDB();
            return;
        }
        try (Reader reader = new FileReader(dbFile)) {
            Type type = new TypeToken<Map<UUID, PlayerData>>() {}.getType();
            database = gson.fromJson(reader, type);
            if (database == null) {
                database = new HashMap<>();
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
            database = new HashMap<>();
        }
    }

    public void saveDB() {
        try (Writer writer = new FileWriter(dbFile)) {
            gson.toJson(database, writer);
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    public void addPlayer(UUID playerUUID) {
        loadDB();
        if (!database.containsKey(playerUUID)) {
            PlayerData newData = new PlayerData();
            database.put(playerUUID, newData);
            saveDB();
        }
    }

    public boolean playerExists(UUID playerUUID) {
        loadDB();
        return database.containsKey(playerUUID);
    }

    public PlayerData getPlayer(UUID playerUUID) {
        loadDB();
        return database.getOrDefault(playerUUID, null);
    }

    public void savePlayer(UUID playerUUID, PlayerData playerData) {
        loadDB();
        database.put(playerUUID, playerData);
        saveDB();
    }

    public boolean giveDaily(UUID playerUUID) {
        loadDB();
        int daily = plugin.getConfig().getInt("misc.daily_reward");
        int dailyInc = plugin.getConfig().getInt("misc.daily_adder");
        boolean dailyEnabled = plugin.getConfig().getBoolean("misc.daily_rewards");
        if (!dailyEnabled) {return false;}
        PlayerData playerdb = database.getOrDefault(playerUUID, null);
        if (playerdb == null) {return false;}
        DailyData dailydb = playerdb.daily;
        long currentTime = System.currentTimeMillis();
        if ((currentTime - dailydb.last_daily) >= 86400000 ) {
            double reward = daily + dailydb.daily_increase;
            boolean streakLost = false;
            if ((currentTime - dailydb.last_daily >= 86400000 * 2)) {
                reward = daily;
                dailydb.resetIncrease();
                streakLost = true;
            }

            dailydb.setDaily();
            playerdb.setBalance(playerdb.balance += reward);
            playerdb.addTransaction(new Transaction(1, null, "DAILY EARNING", reward));
            dailydb.increaseDaily(dailyInc);
            saveDB();

            Player player = Bukkit.getPlayer(playerUUID);
            if (streakLost) {
                player.sendMessage("§cYou have lost your daily streak!");
            }

            int streak = (int) Math.round((dailydb.daily_increase + 5) / 5);
            if (streakLost) {streak = 1;}
            String currency = plugin.getConfig().getString("currency.currency");
            player.sendMessage( // a lil bit of this and a lil bit of that ahh
                    "You've earned §6" + reward + " " + currency + "§r for joining daily!\n" +
                            "Keep the streak to earn more every day.\n" +
                            "Your current streak: §b" + streak + " day(s)§r."
            );
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            return true;
        } else {
            return false;
        }
    }

    public static class PlayerData {
        public double balance = 0;
        public DailyData daily = new DailyData();
        public Map<Integer, Transaction> transactions = new HashMap<>();

        public void setBalance(double amount) {
            balance = amount;
        }

        public void addTransaction(Transaction transaction) {    // NOT TESTED FOR ERRORS!!!!!
            long currentTime = System.currentTimeMillis();
            for (Transaction nig : transactions.values()) { // nig: existing transaction
                if (nig.type == transaction.type &&
                        nig.description.equals(transaction.description) &&
                        nig.transactor.equals(transaction.transactor)  &&
                        (currentTime - nig.date) < 60000 // 1 min
                ) {
                    nig.amount += transaction.amount;
                    nig.date = currentTime;
                    return;
                }
            }
            transaction.id = (int) Math.floor(Math.random() * 10000);
            transactions.put(transaction.id, transaction);
            Bukkit.getLogger().info("Issued new transaction (" + transaction.amount + "$): " + transaction.description);
        }

        public void removeTransaction(UUID transactionId) {
            transactions.remove(transactionId);
        }
    }

    public static class DailyData {
        public long last_daily = 0;
        public double daily_increase = 0;

        public void setDaily() {setDaily(System.currentTimeMillis());}
        public void setDaily(Long epoch) {
            last_daily = epoch;
        }
        public void increaseDaily(int inc) {daily_increase += inc;}
        public void resetIncrease() {
            daily_increase = 0;
        }
    }

    public static class Transaction {
        public int id;
        public int type; // 0 out, 1 in
        public UUID transactor;
        public String transactorName;
        public long date;
        public String description;
        public double amount;

        public Transaction(int type, @Nullable Player transactor, String description, double amount) {
            this.id = -69;
            this.type = type;
            this.transactor = transactor != null ? transactor.getUniqueId() : new UUID(0, 0);
            this.transactorName = transactor != null ? transactor.getName() : "SERVER";
            this.date = System.currentTimeMillis();
            this.description = description;
            this.amount = amount;
        }
    }
}