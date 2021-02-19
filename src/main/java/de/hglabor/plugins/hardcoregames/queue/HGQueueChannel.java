package de.hglabor.plugins.hardcoregames.queue;

import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.plugins.hardcoregames.util.ChannelIdentifier;
import de.hglabor.plugins.hardcoregames.util.Logger;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class HGQueueChannel extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        Logger.debug(String.format("Redis channel: %s with message %s", channel, message));
        if (channel.equalsIgnoreCase(ChannelIdentifier.HG_QUEUE_LEAVE)) {
            HGPlayer player = PlayerList.INSTANCE.getPlayer(UUID.fromString(message));
            if (player != null && player.getStatus().equals(PlayerStatus.QUEUE)) {
                PlayerList.INSTANCE.remove(UUID.fromString(message));
            }
        } else if (channel.equalsIgnoreCase(ChannelIdentifier.HG_QUEUE_JOIN)) {
            HGQueuePlayerInfo hgQueueJoinInfo = HardcoreGames.GSON.fromJson(message, HGQueuePlayerInfo.class);
            if (Bukkit.getPort() == hgQueueJoinInfo.getPort()) {
                HGPlayer player = PlayerList.INSTANCE.getPlayer(hgQueueJoinInfo);
                player.setStatus(PlayerStatus.QUEUE);
                Logger.debug(String.format("Added to Queue via redis: %s with port %s", player.getName(), hgQueueJoinInfo.getPort()));
            }
        }
    }
}
