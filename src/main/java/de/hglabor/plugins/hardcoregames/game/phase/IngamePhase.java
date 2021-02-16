package de.hglabor.plugins.hardcoregames.game.phase;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.game.mechanics.SkyBorder;
import de.hglabor.plugins.hardcoregames.game.unknown.DeathMessages;
import de.hglabor.plugins.hardcoregames.game.unknown.OfflinePlayerHandler;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.plugins.hardcoregames.util.Logger;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.TeleportUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import de.hglabor.utils.noriskutils.feast.Feast;
import de.hglabor.utils.noriskutils.feast.FeastListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class IngamePhase extends GamePhase {
    protected final OfflinePlayerHandler offlinePlayerManager;
    protected final DeathMessages deathMessages;
    protected final int participants;
    protected final int feastAppearance;
    protected final SkyBorder skyBorder;
    protected Feast feast;
    protected FeastListener feastListener;
    protected Optional<HGPlayer> winner;

    public IngamePhase() {
        super(HGConfig.getInteger(ConfigKeys.INGAME_MAX_PLAYTIME));
        this.skyBorder = new SkyBorder(HGConfig.getInteger(ConfigKeys.SKY_BORDER_DAMAGE));
        this.feastAppearance = ChanceUtils.getRandomNumber(HGConfig.getInteger(ConfigKeys.FEAST_LATEST_APPEARANCE), HGConfig.getInteger(ConfigKeys.FEAST_EARLIEST_APPEARANCE));
        this.offlinePlayerManager = new OfflinePlayerHandler(this);
        this.deathMessages = new DeathMessages();
        this.participants = playerList.getAlivePlayers().size();
    }

    @Override
    protected void init() {
        playerList.getPlayers().stream().filter(player -> player.getStatus().equals(PlayerStatus.OFFLINE)).forEach(offlinePlayerManager::putAndStartTimer);
        KitApi.getInstance().getEnabledKits().forEach(enabledKit -> enabledKit.setUsable(true));
    }

    @Override
    protected void tick(int timer) {
        skyBorder.tick();
        if (timer > maxPhaseTime) {
            checkForWinnerWithMostKills();
        } else {
            if (checkForWinner()) {
                return;
            }
            if (timer == feastAppearance) {
                World world = Bukkit.getWorld("world");
                feastListener = new FeastListener();
                Bukkit.getPluginManager().registerEvents(feastListener, HardcoreGames.getPlugin());
                feast = new Feast(HardcoreGames.getPlugin(), world).center(TeleportUtils.getHighestRandomLocation(world, 200, -200))
                        .material(Material.GRASS_BLOCK)
                        .radius(20)
                        .timer(HGConfig.getInteger(ConfigKeys.FEAST_TIME_TILL_SPAWN))
                        .air(20);
                feast.spawn();
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        HGPlayer hgPlayer = playerList.getPlayer(player);
        Logger.debug(String.format("%s joined with status %s in phase %s", hgPlayer.getName(), hgPlayer.getStatus(), this.getType()));
        switch (hgPlayer.getStatus()) {
            case ELIMINATED:
                if (player.hasPermission("hglabor.spectator")) break;
                event.setKickMessage(Localization.INSTANCE.getMessage("ingamePhase.eliminated", ChatUtils.getPlayerLocale(player)));
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                break;
            case WAITING:
                if (player.hasPermission("hglabor.spectator")) {
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
        Player player = event.getPlayer();
        HGPlayer hgPlayer = playerList.getPlayer(player);
        switch (hgPlayer.getStatus()) {
            case OFFLINE:
                offlinePlayerManager.stopTimer(hgPlayer);
                hgPlayer.setStatus(PlayerStatus.ALIVE);
                break;
            case SPECTATOR:
                event.setJoinMessage(null);
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(Localization.INSTANCE.getMessage("ingamePhase.roundHasStarted", ChatUtils.getPlayerLocale(player)));
                break;
        }
        Logger.debug(String.format("%s joined with status %s in phase %s", hgPlayer.getName(), hgPlayer.getStatus(), this.getType()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HGPlayer hgPlayer = playerList.getPlayer(player);
        if (hgPlayer.getStatus().equals(PlayerStatus.ALIVE)) {
            if (hgPlayer.isInCombat()) {
                Logger.debug(String.format("%s combatlogged", player.getName()));
                player.setHealth(0);
            } else {
                hgPlayer.setStatus(PlayerStatus.OFFLINE);
                offlinePlayerManager.putAndStartTimer(hgPlayer);
            }
        }
    }

    @EventHandler
    public void onAlivePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        HGPlayer hgPlayer = playerList.getPlayer(player);

        if (killer != null) {
            PlayerList.INSTANCE.getPlayer(killer).getKills().getAndIncrement();
            Logger.debug(String.format("%s killed %s mc wise", killer.getName(), player.getName()));
        } else {
            hgPlayer.getLastHitInformation().getLastDamager().ifPresent(p -> {
                PlayerList.INSTANCE.getPlayer(p).getKills().getAndIncrement();
                Logger.debug(String.format("%s killed %s lastdamager wise", p.getName(), player.getName()));
            });
        }

        if (hgPlayer.getStatus().equals(PlayerStatus.ALIVE)) {
            hgPlayer.setStatus(PlayerStatus.ELIMINATED);

            deathMessages.broadcastDeathMessage(player);
            final int PLAYERS_LEFT = playerList.getAlivePlayers().size();
            if (PLAYERS_LEFT != 1) {
                ChatUtils.broadcastMessage("ingamePhase.playersLeft", ImmutableMap.of("playersLeft", String.valueOf(PLAYERS_LEFT)));
            }
            if (player.hasPermission("hglabor.spectator")) {
                //TODO hiden player and dont allow writing
                hgPlayer.setStatus(PlayerStatus.SPECTATOR);
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.kickPlayer(Localization.INSTANCE.getMessage("ingamePhase.thanksForPlaying", hgPlayer.getLocale()));
            }

            checkForWinner();
        }
    }

    private void checkForWinnerWithMostKills() {
        List<HGPlayer> alivePlayers = playerList.getAlivePlayers();
        Collections.shuffle(alivePlayers);
        this.offlinePlayerManager.stopAll();
        this.winner = alivePlayers.stream().max(Comparator.comparingInt(value -> value.getKills().get()));
        this.killEveryoneExceptWinner();
    }

    private void killEveryoneExceptWinner() {
        for (HGPlayer hgPlayer : playerList.getAlivePlayers()) {
            if (winner.isPresent() && winner.get().getUUID().equals(hgPlayer.getUUID())) continue;
            hgPlayer.getBukkitPlayer().ifPresentOrElse(player -> player.setHealth(0), () -> hgPlayer.setStatus(PlayerStatus.ELIMINATED));
        }
    }

    public boolean checkForWinner() {
        if (playerList.getAlivePlayers().size() <= 1) {
            this.offlinePlayerManager.stopAll();
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
    protected String getTimeString(int timer) {
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
    protected GamePhase getNextPhase() {
        return new EndPhase(winner, participants);
    }
}
