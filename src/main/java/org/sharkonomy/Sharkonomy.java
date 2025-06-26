package org.sharkonomy;

import org.bukkit.plugin.java.JavaPlugin;
import org.sharkonomy.commands.economy.economy;
import org.sharkonomy.commands.economy.economyTab;
import org.sharkonomy.listeners.rightClick;
import org.sharkonomy.utils.PluginData;
import org.sharkonomy.listeners.playerJoin;
import org.sharkonomy.listeners.prepareItemCraft;

public final class Sharkonomy extends JavaPlugin {
    private PluginData database;
    private static Sharkonomy instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getLogger().info("Initializing...");
        database = new PluginData(this);
        getServer().getPluginManager().registerEvents(new playerJoin(this), this);
        getServer().getPluginManager().registerEvents(new prepareItemCraft(this), this);
        getServer().getPluginManager().registerEvents(new rightClick(this), this);
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

    public PluginData getDatabase() {
        return database;
    }

}
