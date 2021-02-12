package de.hglabor.plugins.hardcoregames;

import com.google.gson.Gson;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.listener.PlayerJoinListener;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.queue.QueueListener;
import de.hglabor.plugins.hardcoregames.queue.ServerPingListener;
import de.hglabor.plugins.hardcoregames.scoreboard.ScoreboardManager;
import de.hglabor.plugins.hardcoregames.util.ChannelIdentifier;
import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.listener.LastHitDetection;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.listener.DamageNerf;
import de.hglabor.utils.noriskutils.listener.DurabilityFix;
import de.hglabor.utils.noriskutils.listener.OldKnockback;
import de.hglabor.utils.noriskutils.listener.RemoveHitCooldown;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public final class HardcoreGames extends JavaPlugin {
    public static final Gson GSON = new Gson();
    public static HardcoreGames plugin;

    public static HardcoreGames getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        Localization.INSTANCE.loadLanguageFiles(Paths.get(this.getDataFolder() + "/lang"), "\u00A7");
        HGConfig.load();
        KitApiConfig.getInstance().register(this.getDataFolder());

        // KitManager.getInstance().register(PlayerList.getInstance(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, ChannelIdentifier.HG_QUEUE);
        this.registerEvents();

        GameStateManager.INSTANCE.run();

        for (Player player : Bukkit.getOnlinePlayers()) {
            HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(player);
            ScoreboardFactory.create(hgPlayer);
            ScoreboardManager.setBasicScoreboardLayout(hgPlayer);
        }
    }

    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        // KitManager.getInstance().getEnabledKits().stream().filter(enabledKit -> enabledKit instanceof Listener).forEach(enabledKit -> pluginManager.registerEvents((Listener) enabledKit, this));
        pluginManager.registerEvents(new ServerPingListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new QueueListener(), this);
        pluginManager.registerEvents(new RemoveHitCooldown(), this);
        pluginManager.registerEvents(new OldKnockback(this), this);
        pluginManager.registerEvents(new DurabilityFix(), this);
        pluginManager.registerEvents(new DamageNerf(), this);
        pluginManager.registerEvents(new LastHitDetection(), this);
    }

    @Override
    public void onDisable() {
    }
}
