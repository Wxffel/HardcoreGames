package de.hglabor.plugins.hardcoregames.listener;

import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.scoreboard.ScoreboardManager;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(event.getPlayer());
        //TODO nur create if er nicht hat
        ScoreboardFactory.create(hgPlayer);
        ScoreboardManager.setBasicScoreboardLayout(hgPlayer);
    }
}
