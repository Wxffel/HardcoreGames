package de.hglabor.plugins.hardcoregames.game.unknown;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.util.Logger;
import de.hglabor.plugins.kitapi.kit.config.LastHitInformation;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

public class DeathMessages {
    public DeathMessages() {
    }

    public void broadcastDeathMessage(Player player) {
        if (player.getLastDamageCause() == null) {
            return;
        }
        HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(player);
        LastHitInformation lastHitInformation = hgPlayer.getLastHitInformation();
        Logger.debug(String.format("%s lastdamagetimestamp: %s",player.getName(),lastHitInformation.getLastDamagerTimestamp()));
        if (lastHitInformation.getLastDamager().isPresent() && lastHitInformation.getLastDamagerTimestamp() + 10 * 1000L > System.currentTimeMillis()) {
            Player killer = lastHitInformation.getLastDamager().get();
            HGPlayer hgKiller = PlayerList.INSTANCE.getPlayer(killer);
            printDeathMessage(player.getLastDamageCause().getCause(), "byPlayer", ImmutableMap.of(
                    "playerName", hgPlayer.getName(),
                    "playerKit", hgPlayer.printKits(),
                    "killerName", hgKiller.getName(),
                    "killerKit", hgKiller.printKits()));
        } else {
            printDeathMessage(player.getLastDamageCause().getCause(), "", ImmutableMap.of(
                    "playerName", hgPlayer.getName(),
                    "playerKit", hgPlayer.printKits()));
        }
    }

    private void printDeathMessage(EntityDamageEvent.DamageCause damageCause, String keyInfo, Map<String, String> values) {
        switch (damageCause) {
            case FALL:
                ChatUtils.broadcastMessage("deathmessage.FALL" + keyInfo, values);
                break;
            case PROJECTILE:
                ChatUtils.broadcastMessage("deathmessage.PROJECTILE" + keyInfo, values);
                break;
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                ChatUtils.broadcastMessage("deathmessage.EXPLOSION" + keyInfo, values);
                break;
            case LAVA:
            case FIRE:
            case FIRE_TICK:
                ChatUtils.broadcastMessage("deathmessage.FIRE" + keyInfo, values);
                break;
            case SUICIDE:
                ChatUtils.broadcastMessage("deathmessage.SUICIDE" + keyInfo, values);
                break;
            case CONTACT:
                ChatUtils.broadcastMessage("deathmessage.CONTACT" + keyInfo, values);
                break;
            case SUFFOCATION:
            case FALLING_BLOCK:
                ChatUtils.broadcastMessage("deathmessage.SUFFACTION" + keyInfo, values);
                break;
            case WITHER:
                ChatUtils.broadcastMessage("deathmessage.WITHER" + keyInfo, values);
                break;
            case DROWNING:
                ChatUtils.broadcastMessage("deathmessage.DROWNING" + keyInfo, values);
                break;
            case LIGHTNING:
                ChatUtils.broadcastMessage("deathmessage.LIGHTNING" + keyInfo, values);
                break;
            default:
                ChatUtils.broadcastMessage("deathmessage.DEFAULT" + keyInfo, values);
                break;
        }
    }
}
