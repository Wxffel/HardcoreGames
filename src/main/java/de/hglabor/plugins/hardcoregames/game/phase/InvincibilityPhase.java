package de.hglabor.plugins.hardcoregames.game.phase;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import de.hglabor.utils.noriskutils.PotionUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class InvincibilityPhase extends GamePhase {
    protected final ItemStack tracker;
    protected int timeLeft;

    public InvincibilityPhase() {
        super(HGConfig.getInteger(ConfigKeys.INVINCIBILITY_TIME));
        this.tracker = new ItemBuilder(Material.COMPASS).setName("Tracker").build();
    }

    @Override
    protected void init() {
        Optional<World> world = Optional.ofNullable(Bukkit.getWorld("world"));
        world.ifPresent(HGConfig::inGameWorldSettings);
        playerList.getWaitingPlayers().forEach(alivePlayer -> alivePlayer.setStatus(PlayerStatus.ALIVE));
        for (HGPlayer alivePlayer : playerList.getAlivePlayers()) {
            for (AbstractKit kit : alivePlayer.getKits()) {
                alivePlayer.getBukkitPlayer().ifPresent(player -> {
                    player.closeInventory();
                    player.getInventory().clear();
                    kit.getKitItems().forEach(item -> player.getInventory().addItem(item));
                    kit.enable(alivePlayer);
                });
            }
            alivePlayer.getBukkitPlayer().ifPresent(player -> {
                PotionUtils.removePotionEffects(player);
                player.getInventory().addItem(tracker);
            });
        }
    }

    @Override
    protected void tick(int timer) {
        timeLeft = maxPhaseTime - timer;

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
    public int getRawTime() {
        return timeLeft;
    }

    @Override
    protected String getTimeString(int timer) {
        return TimeConverter.stringify(timeLeft);
    }

    @Override
    public int getMaxParticipants() {
        return getCurrentParticipants();
    }

    @Override
    public int getCurrentParticipants() {
        return (int) playerList.getPlayers().stream().filter(hgPlayer -> hgPlayer.getStatus().equals(PlayerStatus.ALIVE) || hgPlayer.getStatus().equals(PlayerStatus.OFFLINE)).count();
    }

    @Override
    protected GamePhase getNextPhase() {
        return new IngamePhase();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HGPlayer hgPlayer = playerList.getPlayer(player);
        if (hgPlayer.getStatus().equals(PlayerStatus.WAITING)) {
            PotionUtils.removePotionEffects(player);
            player.getInventory().clear();
            player.getInventory().addItem(tracker);
            player.sendMessage(Localization.INSTANCE.getMessage("invincibilityPhase.hasStarted", hgPlayer.getLocale()));
            hgPlayer.setStatus(PlayerStatus.ALIVE);
            hgPlayer.teleportToSafeSpawn();
        } else if (!hgPlayer.getStatus().equals(PlayerStatus.SPECTATOR)) {
            hgPlayer.setStatus(PlayerStatus.ALIVE);
        } else {
            //TODO message he is in spectator mode
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        if (hgPlayer.getStatus().equals(PlayerStatus.ALIVE)) {
            hgPlayer.setStatus(PlayerStatus.OFFLINE);
        }
    }

    @EventHandler
    private void onPlayerReceivesDamage(EntityDamageEvent event) {
        event.setCancelled(event.getEntity() instanceof Player);
    }

    @EventHandler
    private void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(event.getEntity() instanceof Player);
    }
}
