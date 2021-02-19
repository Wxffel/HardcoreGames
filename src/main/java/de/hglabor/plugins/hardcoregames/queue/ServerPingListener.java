package de.hglabor.plugins.hardcoregames.queue;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.utils.noriskutils.queue.hg.HGGameInfo;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerPingListener implements Listener {

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        HGGameInfo hgInfo = new HGGameInfo(
                Bukkit.getMaxPlayers(),
                GameStateManager.INSTANCE.getPhase().getCurrentParticipants(),
                Bukkit.getPort(),
                GameStateManager.INSTANCE.getPhase().getRawTime(),
                GameStateManager.INSTANCE.getPhase().getType().name());
        event.setMotd(HardcoreGames.GSON.toJson(hgInfo, HGGameInfo.class));
    }
}
