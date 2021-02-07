package de.hglabor.plugins.hardcoregames.listener;

import de.hglabor.plugins.hardcoregames.queue.QueueListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().addItem(QueueListener.QUEUE_ITEM);
    }
}

