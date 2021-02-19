package de.hglabor.plugins.hardcoregames.game.phase;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
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
    protected final int participants;
    private final Optional<HGPlayer> winner;
    private final int endTime;

    public EndPhase(Optional<HGPlayer> winner, int participants) {
        super(HGConfig.getInteger(ConfigKeys.END_RESTART_AFTER));
        this.endTime = GameStateManager.INSTANCE.getTimer();
        this.winner = winner;
        this.participants = participants;
    }

    @Override
    protected void init() {
        winner.ifPresent(hgPlayer -> {
            Player player = Bukkit.getPlayer(hgPlayer.getUUID());
            if (player != null) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        });
        GameStateManager.INSTANCE.resetTimer();
    }

    @Override
    protected void tick(int timer) {
        if (timer <= maxPhaseTime) {
            winner.ifPresentOrElse(hgPlayer -> {
                ChatUtils.broadcastMessage("endPhase.winAnnouncementPlayer", ImmutableMap.of("player", hgPlayer.getName()));
            }, () -> {
                ChatUtils.broadcastMessage("endPhase.winAnnouncementNobody");
            });
        } else {
            //TODO ANNOUNCE BUNGEECORD WINNER
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }
    }

    @Override
    public PhaseType getType() {
        return PhaseType.END;
    }

    @Override
    public int getRawTime() {
        return endTime;
    }

    @Override
    protected String getTimeString(int timer) {
        return TimeConverter.stringify(endTime);
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
        return null;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}
