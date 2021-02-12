package de.hglabor.plugins.hardcoregames.game.phases;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.DeathMessages;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.OfflinePlayerManager;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Comparator;
import java.util.Optional;

public class IngamePhase extends GamePhase {
    protected final OfflinePlayerManager offlinePlayerManager;
    protected final DeathMessages deathMessages;
    protected final int participants;
    protected Optional<HGPlayer> winner;

    public IngamePhase(int invincibilityTime) {
        super(HGConfig.getInteger(ConfigKeys.INGAME_MAX_PLAYTIME) + invincibilityTime);
        this.offlinePlayerManager = new OfflinePlayerManager(this);
        this.deathMessages = new DeathMessages();
        this.participants = playerList.getAlivePlayers().size();
    }

    @Override
    public void init() {
        playerList.getPlayers().stream().filter(player -> player.getStatus().equals(PlayerStatus.OFFLINE)).forEach(offlinePlayerManager::putAndStartTimer);
    }

    @Override
    public void tick(int timer) {
        if (timer >= maxPhaseTime) {
            checkForWinnerWithMostKills();
        } else {
            if (checkForWinner()) {
                return;
            }
            //TODO FEAST
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        HGPlayer hgPlayer = playerList.getPlayer(player);
        switch (hgPlayer.getStatus()) {
            case ELIMINATED:
                event.setKickMessage(Localization.INSTANCE.getMessage("ingamePhase.eliminated", ChatUtils.getPlayerLocale(player)));
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                break;
            case WAITING:
                if (player.hasPermission("hglabor.hg.spectator")) {
                    hgPlayer.setStatus(PlayerStatus.SPECTATOR);
                } else {
                    event.setKickMessage(Localization.INSTANCE.getMessage("ingamePhase.roundHasStarted", ChatUtils.getPlayerLocale(player)));
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        switch (hgPlayer.getStatus()) {
            case OFFLINE:
                offlinePlayerManager.stopTimer(hgPlayer);
                break;
            case SPECTATOR:
                event.setJoinMessage(null);
                break;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        if (hgPlayer.getStatus().equals(PlayerStatus.ALIVE)) {
            hgPlayer.setStatus(PlayerStatus.OFFLINE);
            offlinePlayerManager.putAndStartTimer(hgPlayer);
        }
    }

    @EventHandler
    public void onAlivePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        HGPlayer hgPlayer = playerList.getPlayer(player);
        if (hgPlayer.getStatus().equals(PlayerStatus.ALIVE)) {
            hgPlayer.setStatus(PlayerStatus.ELIMINATED);

            deathMessages.broadcastDeathMessage(player);
            final int PLAYERS_LEFT = playerList.getAlivePlayers().size();
            if (PLAYERS_LEFT != 1) {
                ChatUtils.broadcastMessage("ingamePhase.playersLeft", ImmutableMap.of("playersLeft", String.valueOf(PLAYERS_LEFT)));
            }

            checkForWinner();
        }
    }

    private void checkForWinnerWithMostKills() {
        this.offlinePlayerManager.clear();
        this.winner = playerList.getAlivePlayers().stream().max(Comparator.comparingInt(HGPlayer::getKills));
        this.startNextPhase();
    }

    public boolean checkForWinner() {
        if (playerList.getAlivePlayers().size() <= 1) {
            this.offlinePlayerManager.clear();
            this.winner = playerList.getAlivePlayers().stream().findFirst();
            this.startNextPhase();
            return true;
        }
        return false;
    }

    @Override
    public PhaseType getType() {
        return PhaseType.INGAME;
    }

    @Override
    public String getTimeString(int timer) {
        return TimeConverter.stringify(timer);
    }

    @Override
    public int getMaxParticipants() {
        return participants;
    }

    @Override
    public int getCurrentParticipants() {
        return playerList.getAlivePlayers().size();
    }

    @Override
    public GamePhase getNextPhase() {
        return new EndPhase(winner, participants);
    }
}
