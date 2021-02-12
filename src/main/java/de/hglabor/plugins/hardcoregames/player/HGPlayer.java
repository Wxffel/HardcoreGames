package de.hglabor.plugins.hardcoregames.player;

import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.kitapi.supplier.KitPlayerImpl;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class HGPlayer extends KitPlayerImpl implements ScoreboardPlayer {
    protected final String name;
    protected int kills;
    protected PlayerStatus status;
    protected Scoreboard scoreboard;
    protected Objective objective;
    protected Player lastDamager;
    protected AtomicInteger offlineTime;

    protected HGPlayer(UUID uuid) {
        super(uuid);
        this.offlineTime = new AtomicInteger(HGConfig.getInteger(ConfigKeys.PLAYER_OFFLINE_TIME));
        this.status = PlayerStatus.WAITING;
        Player player = Bukkit.getPlayer(uuid);
        this.name = player != null ? player.getName() : "UNKOWN";
    }

    public void increaseKills() {
        this.kills++;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    //TODO
    @Override
    public boolean isValid() {
        return isWaiting();
    }

    public boolean isWaiting() {
        return status == PlayerStatus.WAITING;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    //TODO yo
    public boolean isInCombat() {
        return false;
    }

    public Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public Objective getObjective() {
        return objective;
    }

    @Override
    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    @Override
    public Locale getLocale() {
        return ChatUtils.getPlayerLocale(uuid);
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Player getLastDamager() {
        return lastDamager;
    }

    public AtomicInteger getOfflineTime() {
        return offlineTime;
    }
}
