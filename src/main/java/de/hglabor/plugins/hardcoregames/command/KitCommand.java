package de.hglabor.plugins.hardcoregames.command;

import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.kits.NoneKit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.entity.Player;

import java.util.Optional;

public class KitCommand {

    public KitCommand() {
        for (int i = 0; i < KitApiConfig.getInstance().getKitAmount(); i++) {
            int finalI = i;
            new CommandAPICommand("kit" + (i == 0 ? "" : (i + 1)))
                    .withRequirement(commandSender -> {
                        if (commandSender instanceof Player) {
                            HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer((Player) commandSender);
                            if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.LOBBY)) {
                                return true;
                            } else if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.INVINCIBILITY)) {
                                return hgPlayer.getKits().get(finalI).equals(NoneKit.getInstance());
                            }
                            return false;
                        }
                        return false;
                    })
                    .withArguments(kitArgument("None"))
                    .executesPlayer((player, objects) -> {
                        player.sendMessage(((AbstractKit) objects[0]).getName());
                    })
                    .register();
        }
    }

    public Argument kitArgument(String kitName) {
        return new CustomArgument<>(kitName, (input) -> {
            Optional<AbstractKit> kitInput = KitManager.getInstance().getEnabledKits().stream().filter(kit -> kit.getName().equalsIgnoreCase(input)).findFirst();
            if (kitInput.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown kit: ").appendArgInput());
            } else {
                return kitInput.get();
            }
        }).overrideSuggestions(sender -> KitManager.getInstance().getEnabledKits().stream().map(AbstractKit::getName).toArray(String[]::new));
    }
}
