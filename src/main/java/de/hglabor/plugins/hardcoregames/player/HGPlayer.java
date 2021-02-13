package de.hglabor.plugins.hardcoregames.player;

import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.phase.LobbyPhase;
import de.hglabor.plugins.kitapi.supplier.KitPlayerImpl;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer;
import de.hglabor.utils.noriskutils.staffmode.StaffModeManager;
import de.hglabor.utils.noriskutils.staffmode.StaffPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class HGPlayer extends KitPlayerImpl implements ScoreboardPlayer, StaffPlayer {
    protected final String name;
    protected int kills;
    protected boolean isStaffMode;
    protected boolean isVisible;
    protected boolean canSeeStaffModePlayers;
    protected PlayerStatus status;
    protected Scoreboard scoreboard;
    protected Objective objective;
    protected AtomicInteger offlineTime;

    protected HGPlayer(UUID uuid, String name) {
        super(uuid);
        this.name = name;
        this.offlineTime = new AtomicInteger(HGConfig.getInteger(ConfigKeys.PLAYER_OFFLINE_TIME));
        this.status = PlayerStatus.WAITING;
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

    @Override
    public boolean isStaffMode() {
        return isStaffMode;
    }

    @Override
    public void toggleStaffMode() {
        if (isStaffMode) {
            isStaffMode = false;
            getBukkitPlayer().ifPresent(player -> {
                player.sendMessage(Localization.INSTANCE.getMessage("staffmode.disabled", getLocale()));
                switch (GameStateManager.INSTANCE.getPhase().getType()) {
                    case LOBBY:
                        ((LobbyPhase) GameStateManager.INSTANCE.getPhase()).setPlayerLobbyReady(player);
                        StaffModeManager.INSTANCE.getPlayerHider().show(player);
                        //TODO SICHTBARMACHEN
                        break;
                    case INVINCIBILITY:
                        status = PlayerStatus.ALIVE;
                        player.setGameMode(GameMode.SURVIVAL);
                        player.getInventory().clear();
                        StaffModeManager.INSTANCE.getPlayerHider().show(player);
                        //TODO SICHTBARMACHEN
                        break;
                    default:
                        player.sendMessage(Localization.INSTANCE.getMessage("staffmode.stayInStaffMode", getLocale()));
                        break;
                }
            });
        } else {
            isStaffMode = true;
            getBukkitPlayer().ifPresent(player -> {
                player.sendMessage(Localization.INSTANCE.getMessage("staffmode.enabled", getLocale()));
                player.setGameMode(GameMode.CREATIVE);
                player.getInventory().clear();
                StaffModeManager.INSTANCE.getPlayerHider().hide(player);
                StaffModeManager.INSTANCE.getStaffModeItems().forEach(staffModeItem -> player.getInventory().addItem(staffModeItem));
                switch (GameStateManager.INSTANCE.getPhase().getType()) {
                    case LOBBY:
                    case INVINCIBILITY:
                        status = PlayerStatus.SPECTATOR;
                        //TODO EXCLUDED FROM GAME UND SO
                        break;
                    case INGAME:
                        if (status.equals(PlayerStatus.ALIVE)) {
                            player.setHealth(0);
                        }
                        break;
                }
            });
        }
    }

    @Override
    public void printStatsOf(Player toPrint) {
        getBukkitPlayer().ifPresent(player -> {
            player.sendMessage(toPrint.getName());
        });
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public void setCanSeeStaffModePlayers(boolean value) {
        this.canSeeStaffModePlayers = value;
    }

    @Override
    public boolean canSeeStaffModePlayers() {
        return canSeeStaffModePlayers;
    }
}
