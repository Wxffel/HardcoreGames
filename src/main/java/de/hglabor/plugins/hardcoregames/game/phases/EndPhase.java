package de.hglabor.plugins.hardcoregames.game.phases;

import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;

public class EndPhase extends GamePhase {
    private final Optional<HGPlayer> winner;

    public EndPhase(Optional<HGPlayer> winner) {
        this.winner = winner;
    }

    @Override
    public void init() {
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
        winner.ifPresent(hgPlayer -> {
            Player player = Bukkit.getPlayer(hgPlayer.getUUID());
            if (player != null) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        });
    }

    @Override
    public void tick(int timer) {

    }

    @Override
    public PhaseType getType() {
        return PhaseType.END;
    }

    @Override
    public GamePhase getNextPhase() {
        return null;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}
