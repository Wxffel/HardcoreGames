package de.hglabor.plugins.hardcoregames.queue;

import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.plugins.hardcoregames.util.Logger;
import de.hglabor.utils.noriskutils.jedis.JChannels;
import de.hglabor.utils.noriskutils.queue.hg.HGQueuePlayerInfo;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class HGQueueChannel extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        Logger.debug(String.format("Redis channel: %s with message %s", channel, message));
        HGPlayer hgPlayer;
        switch (channel) {
            case JChannels.HGQUEUE_LEAVE:
                hgPlayer = PlayerList.INSTANCE.getPlayer(UUID.fromString(message));
                if (hgPlayer != null && hgPlayer.getStatus().equals(PlayerStatus.QUEUE)) {
                    PlayerList.INSTANCE.remove(UUID.fromString(message));
                }
                break;
            case JChannels.HGQUEUE_JOIN:
                HGQueuePlayerInfo hgQueueJoinInfo = HardcoreGames.GSON.fromJson(message, HGQueuePlayerInfo.class);
                if (Bukkit.getPort() == hgQueueJoinInfo.getPort()) {
                    hgPlayer = PlayerList.INSTANCE.getPlayer(hgQueueJoinInfo);
                    hgPlayer.setStatus(PlayerStatus.QUEUE);
                    Logger.debug(String.format("Added to Queue via redis: %s with port %s", hgPlayer.getName(), hgQueueJoinInfo.getPort()));
                }
                break;
        }
    }
}
