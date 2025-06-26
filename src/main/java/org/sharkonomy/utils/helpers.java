package org.sharkonomy.utils;

import org.bukkit.Material;

public class helpers {
    public static String formatMatName(Material material) {
        String name = material.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
