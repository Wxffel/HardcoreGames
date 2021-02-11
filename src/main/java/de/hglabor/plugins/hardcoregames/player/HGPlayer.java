package de.hglabor.plugins.hardcoregames.player;

import de.hglabor.plugins.kitapi.supplier.KitPlayerImpl;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Locale;
import java.util.UUID;

public class HGPlayer extends KitPlayerImpl implements ScoreboardPlayer {
    protected final String name;
    protected int kills;
    protected Status status;
    protected Scoreboard scoreboard;
    protected Objective objective;

    protected HGPlayer(UUID uuid) {
        super(uuid);
        this.status = Status.WAITING;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
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
        return status == Status.WAITING;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    //TODO yo
    public boolean isInCombat() {
        return false;
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

    public enum Status {
        WAITING, STAFFMODE, SPECTATOR,
    }
}
