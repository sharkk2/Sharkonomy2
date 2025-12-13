package org.sharkonomy.commands.economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class economyTab implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("balance");
            completions.add("pay");
            completions.add("withdraw");
            completions.add("deposit");
            completions.add("transactions");
            completions.add("leaderboard");

        } else if (args.length == 2 && args[0].equalsIgnoreCase("pay")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }

        } else if (args.length == 3 && args[0].equalsIgnoreCase("pay")) {
            completions.add("5");
            completions.add("10");
            completions.add("20");
            completions.add("40");
            completions.add("50");
            completions.add("100");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("withdraw")) {
            completions.add("5");
            completions.add("10");
            completions.add("20");
            completions.add("40");
            completions.add("50");
            completions.add("100");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("deposit")) {
            completions.add("5");
            completions.add("10");
            completions.add("20");
            completions.add("40");
            completions.add("50");
            completions.add("100");
        }
        return completions;
    }
}
