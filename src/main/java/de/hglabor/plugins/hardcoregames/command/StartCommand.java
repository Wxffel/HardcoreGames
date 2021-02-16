package de.hglabor.plugins.hardcoregames.command;

import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.game.phase.LobbyPhase;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.PermissionUtils;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class StartCommand {

    public StartCommand() {
        new CommandAPICommand("start")
                .withAliases("fs", "forcestart", "begin")
                .withPermission("hglabor.forcestart")
                .withRequirement(commandSender -> {
                    if (commandSender instanceof Player) {
                        //commandSender.sendMessage(Localization.INSTANCE.getMessage("permissions.higherStaffIsOnline", ChatUtils.getPlayerLocale(commandSender)));
                        return !PermissionUtils.checkForHigherRank((Player) commandSender);
                    }
                    return true;
                })
                .withRequirement((commandSender) -> GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.LOBBY))
                .withRequirement((commandSender) -> {
                    LobbyPhase lobbyPhase = (LobbyPhase) GameStateManager.INSTANCE.getPhase();
                    return !lobbyPhase.isStarting();
                })
                .executesPlayer((player, objects) -> {
                    LobbyPhase lobbyPhase = (LobbyPhase) GameStateManager.INSTANCE.getPhase();
                    lobbyPhase.setStarting(true);
                    GameStateManager.INSTANCE.setTimer(HGConfig.getInteger(ConfigKeys.COMMAND_FORCESTART_TIME));
                    player.sendMessage(Localization.INSTANCE.getMessage("permissions.roundHasBeenStarted", ChatUtils.getPlayerLocale(player)));
                })
                .register();
    }
}
