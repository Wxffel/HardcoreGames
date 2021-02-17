package de.hglabor.plugins.hardcoregames.queue;

import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.game.phase.LobbyPhase;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.util.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class QueueListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.LOBBY)) {
            LobbyPhase lobbyPhase = (LobbyPhase) GameStateManager.INSTANCE.getPhase();
            if (!lobbyPhase.isStarting()) {
                HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(player);
                PlayerList.INSTANCE.remove(hgPlayer);
                Logger.debug(String.format("%s removed from queue", player.getName()));
            }
        }
    }
}
