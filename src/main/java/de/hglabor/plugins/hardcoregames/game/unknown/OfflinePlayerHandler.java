package de.hglabor.plugins.hardcoregames.game.unknown;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.game.phase.IngamePhase;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.plugins.hardcoregames.util.Logger;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OfflinePlayerHandler {
    private final Map<UUID, BukkitTask> offlinePlayers;
    private final IngamePhase ingamePhase;

    public OfflinePlayerHandler(IngamePhase ingamePhase) {
        this.ingamePhase = ingamePhase;
        this.offlinePlayers = new HashMap<>();
    }

    public void putAndStartTimer(final HGPlayer hgPlayer) {
        Logger.debug(String.format("Starting OfflineTimer for %s", hgPlayer.getName()));
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (hgPlayer.getOfflineTime().getAndDecrement() <= 0 && !isCancelled()) {
                    eliminate(hgPlayer);
                    ingamePhase.checkForWinner();
                    cancel();
                }
            }
        }.runTaskTimer(HardcoreGames.getPlugin(), 0, 20L);
        offlinePlayers.put(hgPlayer.getUUID(), bukkitTask);
    }

    private void eliminate(HGPlayer hgPlayer) {
        hgPlayer.setStatus(PlayerStatus.ELIMINATED);
        ChatUtils.broadcastMessage("ingamePhase.playerDisconnectedForTooLong", ImmutableMap.of("player", hgPlayer.getName()));
        ChatUtils.broadcastMessage("ingamePhase.playersLeft", ImmutableMap.of("playersLeft", String.valueOf(PlayerList.INSTANCE.getAlivePlayers().size())));
        offlinePlayers.remove(hgPlayer.getUUID());
        Logger.debug(String.format("Eliminating via OfflineTimer for %s, his status %s", hgPlayer.getName(), hgPlayer.getStatus()));
    }

    public void stopTimer(final HGPlayer hgPlayer) {
        Optional.ofNullable(offlinePlayers.get(hgPlayer.getUUID())).ifPresent(BukkitTask::cancel);
        offlinePlayers.remove(hgPlayer.getUUID());
    }

    public void stopAll() {
        offlinePlayers.values().forEach(BukkitTask::cancel);
        for (UUID uuid : offlinePlayers.keySet()) {
            Optional<HGPlayer> first = PlayerList.INSTANCE.getAlivePlayers().stream().filter(hgPlayer -> hgPlayer.getUUID().equals(uuid)).findFirst();
            first.ifPresent(hgPlayer -> {
                if (hgPlayer.getStatus().equals(PlayerStatus.OFFLINE)) {
                    eliminate(hgPlayer);
                    ingamePhase.checkForWinner();
                }
            });
        }
        offlinePlayers.clear();
    }
}
