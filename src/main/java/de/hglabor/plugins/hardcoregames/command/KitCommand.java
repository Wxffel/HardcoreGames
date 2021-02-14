package de.hglabor.plugins.hardcoregames.command;

import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.kits.NoneKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class KitCommand {

    public KitCommand() {
        for (int i = 0; i < KitApiConfig.getInstance().getKitAmount(); i++) {
            int index = i;
            new CommandAPICommand("kit" + (i == 0 ? "" : (i + 1)))
                    .withRequirement(commandSender -> {
                        if (commandSender instanceof Player) {
                            HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer((Player) commandSender);
                            if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.LOBBY)) {
                                return true;
                            } else if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.INVINCIBILITY)) {
                                return hgPlayer.getKits().get(index).equals(NoneKit.getInstance());
                            }
                            return false;
                        }
                        return false;
                    })
                    .withArguments(kitArgument("None"))
                    .executesPlayer((player, objects) -> {
                        KitPlayer kitPlayer = PlayerList.INSTANCE.getKitPlayer(player);
                        AbstractKit kit = (AbstractKit) objects[0];
                        if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.LOBBY)) {
                            kitPlayer.setKit(kit,index);
                        } else if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.INVINCIBILITY)) {
                            kitPlayer.setKit(kit,index);
                            kit.getKitItems().forEach(kitItem -> player.getInventory().addItem(kitItem));
                        }
                    })
                    .register();
        }
    }

    public Argument kitArgument(String kitName) {
        return new CustomArgument<>(kitName, (input) -> {
            Optional<AbstractKit> kitInput = KitApi.getInstance().getEnabledKits().stream().filter(kit -> kit.getName().equalsIgnoreCase(input)).findFirst();
            if (kitInput.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown kit: ").appendArgInput());
            } else {
                return kitInput.get();
            }
        }).overrideSuggestions(sender -> KitApi.getInstance().getEnabledKits().stream().map(AbstractKit::getName).toArray(String[]::new));
    }
}
