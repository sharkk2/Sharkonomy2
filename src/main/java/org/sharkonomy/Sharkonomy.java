package org.sharkonomy;

import org.bukkit.plugin.java.JavaPlugin;
import org.sharkonomy.utils.db;
import org.sharkonomy.listeners.playerJoin;

public final class Sharkonomy extends JavaPlugin {
    private db database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Initializing...");
        database = new db(this);
        getServer().getPluginManager().registerEvents(new playerJoin(this), this);
        getLogger().info("Initialized! Sharkonomy ready");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public db getDatabase() {
        return database;
    }
}
