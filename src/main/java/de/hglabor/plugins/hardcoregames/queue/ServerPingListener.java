package de.hglabor.plugins.hardcoregames.queue;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerPingListener implements Listener {

    private final static AtomicInteger counter = new AtomicInteger(120);

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        HGInfo hgInfo = new HGInfo(
                Bukkit.getMaxPlayers(),
                Bukkit.getOnlinePlayers().size(),
                counter.decrementAndGet(),
                "LOBBY",
                Bukkit.getPort());
        event.setMotd(HardcoreGames.GSON.toJson(hgInfo, HGInfo.class));
        if (counter.get() <= 20) {
            counter.set(120);
        }
    }
}
