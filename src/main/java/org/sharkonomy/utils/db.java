package org.sharkonomy.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.sharkonomy.Sharkonomy;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class db {

    private final Sharkonomy plugin;
    private final File dbFile;
    private final Gson gson;
    private Map<UUID, PlayerData> database;

    public db(Sharkonomy plugin) {
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

    public static class PlayerData {
        public double balance = 0;
        public DailyData daily = new DailyData();
        public Map<UUID, Transaction> transactions = new HashMap<>();

        public void setBalance(double amount) {
            balance = amount;
        }

        public void addTransaction(Transaction transaction) {    // NOT TESTED FOR ERRORS!!!!!
            long currentTime = System.currentTimeMillis();
            for (Transaction nig : transactions.values()) { // nig: existing transaction
                if (nig.type == transaction.type &&
                        nig.description.equals(transaction.description) &&
                        nig.amount == transaction.amount &&
                        (currentTime - nig.date) < 60000 // 1 min
                ) {

                    nig.amount += transaction.amount;
                    nig.date = currentTime;
                    return;
                }
            }
            transactions.put(transaction.id, transaction);
        }

        public void removeTransaction(UUID transactionId) {
            transactions.remove(transactionId);
        }
    }

    public static class DailyData {
        public long last_daily = 0;
        public double daily_increase = 0;

        public void setDaily() {
            setDaily(System.currentTimeMillis());
        }

        public void setDaily(Long epoch) {
            last_daily = epoch;
        }

        public void increaseDaily(Byte inc) {
            daily_increase += inc;
        }

        public void resetIncrease() {
            daily_increase = 0;
        }
    }

    public static class Transaction {
        public UUID id;
        public int type; // 0 out, 1 in
        public UUID transactor;
        public long date;
        public String description;
        public double amount;

        public Transaction(int type, Player transactor, String description, double amount) {
            this.id = UUID.randomUUID();
            this.type = type;
            this.transactor = transactor.getUniqueId();
            this.date = System.currentTimeMillis();
            this.description = description;
            this.amount = amount;
        }
    }
}
