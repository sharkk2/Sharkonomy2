package org.sharkonomy.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.sharkonomy.Sharkonomy;

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
        if (!database.containsKey(playerUUID)) {
            PlayerData newData = new PlayerData();
            database.put(playerUUID, newData);
            saveDB();
        }
    }

    public boolean playerExists(UUID playerUUID) {
        return database.containsKey(playerUUID);
    }

    public JsonObject getPlayer(UUID playerUUID) {
        if (!playerExists(playerUUID)) {return null;}
        PlayerData playerData = database.get(playerUUID);
        if (playerData == null) {
            return null;
        }
        return gson.toJsonTree(playerData).getAsJsonObject();
    }


    public void savePlayer(UUID playerUUID, JsonObject playerJson) {
        PlayerData playerData = gson.fromJson(playerJson, PlayerData.class);
        database.put(playerUUID, playerData);
        saveDB();
    }

    public static class PlayerData {
        public double balance = 0;
        public Map<String, Object> shop = new HashMap<>();
        public DailyData daily = new DailyData();
        public Map<String, Object> transactions = new HashMap<>();
    }

    public static class DailyData {
        public long last_daily = 0;
        public double daily_increase = 0;
    }
}
