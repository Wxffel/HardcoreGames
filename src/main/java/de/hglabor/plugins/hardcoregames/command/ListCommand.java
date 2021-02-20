package de.hglabor.plugins.hardcoregames.command;

import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.ChatColor;

public class ListCommand {

    public ListCommand() {
        new CommandAPICommand("list")
                .withAliases("playerlist", "players")
                .executesPlayer((player, objects) -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.LOBBY)) {
                        for (HGPlayer hgPlayer : PlayerList.INSTANCE.getWaitingPlayers()) {
                            stringBuilder.append(getChatColor(hgPlayer)).append(hgPlayer.getName()).append(",");
                        }
                    } else {
                        for (HGPlayer hgPlayer : PlayerList.INSTANCE.getAlivePlayers()) {
                            stringBuilder.append(getChatColor(hgPlayer)).append(hgPlayer.getName()).append(",");
                        }
                    }
                    player.sendMessage(stringBuilder.toString());
                })
                .register();
    }

    private ChatColor getChatColor(HGPlayer hgPlayer) {
        switch (hgPlayer.getStatus()) {
            case ALIVE:
            case WAITING:
                return ChatColor.GREEN;
            case OFFLINE:
                return ChatColor.RED;
            default:
                return ChatColor.WHITE;
        }
    }
}
