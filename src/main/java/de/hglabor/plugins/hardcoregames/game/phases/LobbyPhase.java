package de.hglabor.plugins.hardcoregames.game.phases;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
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
    protected int waitingTime;
    protected int requiredPlayerAmount;

    public LobbyPhase() {
        this.waitingTime = HGConfig.getInteger(ConfigKeys.LOBBY_WAITING_TIME);
        this.requiredPlayerAmount = HGConfig.getInteger(ConfigKeys.LOBBY_PLAYERS_NEEDED);
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Optional<World> world = Optional.ofNullable(Bukkit.getWorld("world"));
        world.ifPresent(HGConfig::lobbyWorldSettings);
    }

    @Override
    public void tick(int timer) {
        final int timeLeft = waitingTime - timer;

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
    public GamePhase getNextPhase() {
        return new InvincibilityPhase();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        player.setHealth(20);
        player.setFireTicks(0);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        //TODO KitSelector
        player.getInventory().addItem(QueueListener.QUEUE_ITEM);
        playerList.getPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        playerList.remove(hgPlayer);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }
}
