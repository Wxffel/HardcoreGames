package de.hglabor.plugins.hardcoregames.listener;

import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.scoreboard.ScoreboardManager;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(player);
        if (hgPlayer.getScoreboard() == null) {
            ScoreboardFactory.create(hgPlayer);
            ScoreboardFactory.addPlayerToNoCollision(player, hgPlayer);
            ScoreboardManager.setBasicScoreboardLayout(hgPlayer);
        }
    }
}
