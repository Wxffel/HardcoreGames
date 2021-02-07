package de.hglabor.plugins.hardcoregames;

import com.google.gson.Gson;
import de.hglabor.plugins.hardcoregames.listener.PlayerJoinListener;
import de.hglabor.plugins.hardcoregames.queue.QueueListener;
import de.hglabor.plugins.hardcoregames.queue.ServerPingListener;
import de.hglabor.plugins.hardcoregames.util.ChannelIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardcoreGames extends JavaPlugin {
    public static final Gson GSON = new Gson();
    public static HardcoreGames plugin;

    public static HardcoreGames getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, ChannelIdentifier.HG_QUEUE);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ServerPingListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new QueueListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
