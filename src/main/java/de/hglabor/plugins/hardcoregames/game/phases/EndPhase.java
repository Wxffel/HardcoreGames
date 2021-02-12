package de.hglabor.plugins.hardcoregames.game.phases;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;

public class EndPhase extends GamePhase {
    private final Optional<HGPlayer> winner;

    public EndPhase(Optional<HGPlayer> winner) {
        super(HGConfig.getInteger(ConfigKeys.END_RESTART_AFTER));
        this.winner = winner;
    }

    @Override
    public void init() {
        killEveryoneExceptWinner();
        winner.ifPresentOrElse(hgPlayer -> {
            Player player = Bukkit.getPlayer(hgPlayer.getUUID());
            if (player != null) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        }, () -> {

        });
    }

    @Override
    public void tick(int timer) {
        final int timeLeft = maxPhaseTime - timer;
        if (timeLeft >= 0) {
            winner.ifPresentOrElse(hgPlayer -> {
                ChatUtils.broadcastMessage("endPhase.winAnnouncementPlayer", ImmutableMap.of("player", hgPlayer.getName()));
            }, () -> {
                ChatUtils.broadcastMessage("endPhase.winAnnouncementNobody");
            });
        } else {
            //TODO RESTART
            //TODO ANNOUNCE WINNER
        }
    }

    @Override
    public PhaseType getType() {
        return PhaseType.END;
    }

    @Override
    public String getTimeString(int timer) {
        return TimeConverter.stringify(timer);
    }

    @Override
    public GamePhase getNextPhase() {
        return null;
    }

    private void killEveryoneExceptWinner() {
        for (HGPlayer hgPlayer : playerList.getAlivePlayers()) {
            hgPlayer.getBukkitPlayer().ifPresent(player -> {
                if (winner.isPresent()) {
                    if (!player.getUniqueId().equals(winner.get().getUUID())) {
                        player.setHealth(0);
                    }
                } else {
                    player.setHealth(0);
                }
            });
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}
