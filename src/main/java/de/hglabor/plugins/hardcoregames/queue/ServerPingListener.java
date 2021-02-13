package de.hglabor.plugins.hardcoregames.queue;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerPingListener implements Listener {

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        HGInfo hgInfo = new HGInfo(
                Bukkit.getMaxPlayers(),
                GameStateManager.INSTANCE.getPhase().getCurrentParticipants(),
                GameStateManager.INSTANCE.getTimer(),
                GameStateManager.INSTANCE.getPhase().getType().name(),
                Bukkit.getPort());
        event.setMotd(HardcoreGames.GSON.toJson(hgInfo, HGInfo.class));
    }
}
