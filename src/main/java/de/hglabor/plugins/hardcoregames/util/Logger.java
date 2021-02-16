package de.hglabor.plugins.hardcoregames.util;

import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import org.bukkit.Bukkit;

public final class Logger {
    private Logger() {
    }

    public static void debug(String message) {
        if (HGConfig.getBoolean(ConfigKeys.DEBUG_IS_ENABLED)) {
            System.out.println(message);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("hglabor.debug") || player.isOp()).forEach(player -> player.sendMessage(message));
        }
    }
}
