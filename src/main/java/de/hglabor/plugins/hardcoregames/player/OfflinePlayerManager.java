package de.hglabor.plugins.hardcoregames.player;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OfflinePlayerManager {
    private final Map<UUID, BukkitTask> offlinePlayers;

    public OfflinePlayerManager() {
        this.offlinePlayers = new HashMap<>();
    }

    public void putAndStartTimer(final HGPlayer hgPlayer) {
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (hgPlayer.offlineTime.getAndDecrement() <= 0 && !isCancelled()) {
                    hgPlayer.setStatus(PlayerStatus.ELIMINATED);
                    ChatUtils.broadcastMessage("ingamePhase.playerDisconnectedForTooLong", ImmutableMap.of("player", hgPlayer.name));
                    ChatUtils.broadcastMessage("ingamePhase.playersLeft", ImmutableMap.of("playersLeft", String.valueOf(PlayerList.INSTANCE.getAlivePlayers().size())));
                    cancel();
                }
            }
        }.runTaskTimer(HardcoreGames.getPlugin(), 0, 20L);
        offlinePlayers.put(hgPlayer.getUUID(), bukkitTask);
    }

    public void stopTimer(final HGPlayer hgPlayer) {
        Optional.ofNullable(offlinePlayers.get(hgPlayer.getUUID())).ifPresent(BukkitTask::cancel);
    }

    public void clear() {
        offlinePlayers.clear();
    }
}
