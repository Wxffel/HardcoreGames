package de.hglabor.plugins.hardcoregames.listener;

import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(player);
        switch (GameStateManager.INSTANCE.getPhase().getType()) {
            case LOBBY:
                PlayerList.INSTANCE.remove(hgPlayer);
                break;
            case INVINCIBILITY:
                hgPlayer.setStatus(PlayerStatus.OFFLINE);
                break;
            case INGAME:
                handleQuitInInGamePhase(event, hgPlayer);
                break;
            case END:
                System.out.println("end");
                break;
        }
    }

    public void handleQuitInInGamePhase(PlayerQuitEvent event, HGPlayer hgPlayer) {
        Player player = event.getPlayer();
        if (hgPlayer.isInCombat()) {
            player.damage(Integer.MAX_VALUE, hgPlayer.getLastDamager());
            player.setHealth(0);
        } else {
            switch (hgPlayer.getStatus()) {
                case ALIVE:
                    //TODO Start Offlinetimer
                    break;
                case ELIMINATED:
                case SPECTATOR:
                    event.setQuitMessage(null);
                    break;
                default:
                    break;
            }
        }
    }
}
