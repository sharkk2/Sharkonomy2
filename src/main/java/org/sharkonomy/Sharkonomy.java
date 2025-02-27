package org.sharkonomy;

import org.bukkit.plugin.java.JavaPlugin;
import org.sharkonomy.commands.economy.economy;
import org.sharkonomy.commands.economy.economyTab;
import org.sharkonomy.utils.db;
import org.sharkonomy.listeners.playerJoin;

public final class Sharkonomy extends JavaPlugin {
    private db database;
    private static Sharkonomy instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getLogger().info("Initializing...");
        database = new db(this);
        getServer().getPluginManager().registerEvents(new playerJoin(this), this);
        getCommand("economy").setExecutor(new economy());
        getCommand("economy").setTabCompleter(new economyTab());
        getLogger().info("Initialized! Sharkonomy ready");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Sharkonomy getInstance() {
        return instance;
    }

    public db getDatabase() {
        return database;
    }

}
