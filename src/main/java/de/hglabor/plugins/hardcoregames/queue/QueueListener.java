package de.hglabor.plugins.hardcoregames.queue;

import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.util.ChannelIdentifier;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;

public class QueueListener implements Listener {
    public final static ItemStack QUEUE_ITEM = new ItemBuilder(Material.EMERALD).setName("Queue").build();
    public final static byte[] HG_QUEUE_INFO_BYTES = HardcoreGames.GSON.toJson(new HGQueueInfo(Bukkit.getPort()), HGQueueInfo.class).getBytes(StandardCharsets.UTF_8);

    @EventHandler
    public void onRightClickQueueItem(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().isSimilar(QUEUE_ITEM)) {
            Player player = event.getPlayer();
            player.sendPluginMessage(HardcoreGames.getPlugin(), ChannelIdentifier.HG_QUEUE, HG_QUEUE_INFO_BYTES);
        }
    }
}
