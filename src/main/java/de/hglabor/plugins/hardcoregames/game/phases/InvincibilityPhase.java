package de.hglabor.plugins.hardcoregames.game.phases;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class InvincibilityPhase extends GamePhase {

    public InvincibilityPhase() {
        super(HGConfig.getInteger(ConfigKeys.INVINCIBILITY_TIME));
    }

    @Override
    public void init() {
        Optional<World> world = Optional.ofNullable(Bukkit.getWorld("world"));
        world.ifPresent(HGConfig::inGameWorldSettings);
        playerList.getWaitingPlayers().forEach(alivePlayer -> alivePlayer.setStatus(PlayerStatus.ALIVE));
        //TODO Kititems, compass
    }

    @Override
    public void tick(int timer) {
        final int timeLeft = maxPhaseTime - timer;

        announceRemainingTime(timeLeft);

        if (timeLeft <= 0) {
            this.startNextPhase();
            ChatUtils.broadcastMessage("invincibilityPhase.timeIsUp");
        }
    }

    private void announceRemainingTime(int timeLeft) {
        if (timeLeft % 60 == 0 || timeLeft <= 5) {
            ChatUtils.broadcastMessage("invincibilityPhase.timeAnnouncement", ImmutableMap.of("timeString", TimeConverter.stringify(timeLeft)));
        }
    }

    @Override
    public PhaseType getType() {
        return PhaseType.INVINCIBILITY;
    }

    @Override
    public String getTimeString(int timer) {
        return TimeConverter.stringify(maxPhaseTime - timer);
    }

    @Override
    public GamePhase getNextPhase() {
        return new IngamePhase(maxPhaseTime);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        if (hgPlayer.getStatus().equals(PlayerStatus.WAITING)) {
            //TODO message he can choose a kit
            hgPlayer.setStatus(PlayerStatus.ALIVE);
        } else if (!hgPlayer.getStatus().equals(PlayerStatus.SPECTATOR)) {
            //TODO message he is in spectator mode
            hgPlayer.setStatus(PlayerStatus.ALIVE);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        if (hgPlayer.getStatus().equals(PlayerStatus.ALIVE)) {
            hgPlayer.setStatus(PlayerStatus.OFFLINE);
        }
    }

    @EventHandler
    public void onPlayerReceivesDamage(EntityDamageEvent event) {
        event.setCancelled(event.getEntity() instanceof Player);
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(event.getEntity() instanceof Player);
    }
}
