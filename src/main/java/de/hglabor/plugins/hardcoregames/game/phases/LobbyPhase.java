package de.hglabor.plugins.hardcoregames.game.phases;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class LobbyPhase implements GamePhase, Listener {
    protected int waitingTime;
    protected int requiredPlayerAmount;

    public LobbyPhase() {
        this.waitingTime = HGConfig.getInteger(ConfigKeys.LOBBY_WAITING_TIME);
        this.requiredPlayerAmount = HGConfig.getInteger(ConfigKeys.LOBBY_PLAYERS_NEEDED);
    }

    @Override
    public void tick(int timer) {
        final int timeLeft = waitingTime - timer;

        announceRemainingTime(timeLeft);

        if (timeLeft <= 0) {
            GameStateManager.INSTANCE.resetTimer();
            if (PlayerList.getInstance().getWaitingPlayers().size() >= requiredPlayerAmount) {
                Optional<World> world = Optional.ofNullable(Bukkit.getWorld("world"));
                world.ifPresent(HGConfig::inGameWorldSettings);
                InvincibilityPhase invincibilityPhase = new InvincibilityPhase();
                HandlerList.unregisterAll(this);
                Bukkit.getPluginManager().registerEvents(invincibilityPhase, HardcoreGames.getPlugin());
                GameStateManager.INSTANCE.setPhase(invincibilityPhase);
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
