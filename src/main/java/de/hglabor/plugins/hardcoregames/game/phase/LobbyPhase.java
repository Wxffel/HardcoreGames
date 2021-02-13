package de.hglabor.plugins.hardcoregames.game.phase;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.plugins.hardcoregames.queue.QueueListener;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

import java.util.Optional;

public class LobbyPhase extends GamePhase {
    protected int requiredPlayerAmount;
    protected int timeLeft;

    public LobbyPhase() {
        super(HGConfig.getInteger(ConfigKeys.LOBBY_WAITING_TIME));
        this.requiredPlayerAmount = HGConfig.getInteger(ConfigKeys.LOBBY_PLAYERS_NEEDED);
    }

    @Override
    protected void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Optional<World> world = Optional.ofNullable(Bukkit.getWorld("world"));
        world.ifPresent(HGConfig::lobbyWorldSettings);
    }

    @Override
    protected void tick(int timer) {
        if (playerList.getWaitingPlayers().size() >= requiredPlayerAmount) {
            timeLeft = maxPhaseTime - timer;
            announceRemainingTime(timeLeft);

            if (timeLeft <= 0) {
                GameStateManager.INSTANCE.resetTimer();
                if (PlayerList.INSTANCE.getWaitingPlayers().size() >= requiredPlayerAmount) {
                    this.startNextPhase();
                    ChatUtils.broadcastMessage("lobbyPhase.gameStarts");
                } else {
                    ChatUtils.broadcastMessage("lobbyPhase.notEnoughPlayers", ImmutableMap.of("requiredPlayers", String.valueOf(requiredPlayerAmount)));
                }
            }
        } else {
            GameStateManager.INSTANCE.resetTimer();
        }
    }

    private void announceRemainingTime(int timeLeft) {
        if (timeLeft % 30 == 0 || timeLeft <= 5 || timeLeft == 15 || timeLeft == 10) {
            ChatUtils.broadcastMessage("lobbyPhase.timeAnnouncement", ImmutableMap.of("timeString", TimeConverter.stringify(timeLeft)));
        }
    }

    @Override
    public PhaseType getType() {
        return PhaseType.LOBBY;
    }

    @Override
    public int getRawTime() {
        int rawTime = super.getRawTime();
        return rawTime == 0 ? maxPhaseTime - rawTime : timeLeft;
    }

    @Override
    protected String getTimeString(int timer) {
        return TimeConverter.stringify(getRawTime());
    }

    @Override
    public int getCurrentParticipants() {
        return playerList.getWaitingPlayers().size();
    }

    @Override
    public int getMaxParticipants() {
        return getCurrentParticipants();
    }

    @Override
    protected GamePhase getNextPhase() {
        return new InvincibilityPhase();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        setPlayerLobbyReady(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        playerList.remove(hgPlayer);
    }

    public void setPlayerLobbyReady(Player player) {
        player.getInventory().clear();
        player.setHealth(20);
        player.setFireTicks(0);
        player.setFlying(false);
        player.setTotalExperience(0);
        player.setExp(0);
        player.setAllowFlight(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        //TODO KitSelector
        player.getInventory().addItem(QueueListener.QUEUE_ITEM);
        HGPlayer hgPlayer = playerList.getPlayer(player);
        hgPlayer.setStatus(PlayerStatus.WAITING);
    }

    @EventHandler
    private void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreakEvent(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerPickupExperienceEvent(PlayerPickupExperienceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerAttemptPickupItemEvent(PlayerAttemptPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}