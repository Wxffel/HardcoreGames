package de.hglabor.plugins.hardcoregames.queue;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerPingListener implements Listener {

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        HGInfo hgInfo = new HGInfo(
                Bukkit.getMaxPlayers(),
                Bukkit.getOnlinePlayers().size(),
                60,
                "LOBBY",
                Bukkit.getPort());
        event.setMotd(HardcoreGames.GSON.toJson(hgInfo, HGInfo.class));
    }
}
