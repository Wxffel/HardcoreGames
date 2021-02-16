package de.hglabor.plugins.hardcoregames.game.mechanics;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class Tracker implements Listener {

    @EventHandler
    public void onUseTracker(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Entity target = searchForCompassTarget(player);
        if (event.getMaterial() == Material.COMPASS) {
            if (target == null) {
                player.sendMessage(Localization.INSTANCE.getMessage("hglabor.tracker.noTarget", ChatUtils.getPlayerLocale(player)));
            } else {
                player.setCompassTarget(target.getLocation());
                player.sendMessage(Localization.INSTANCE.getMessage("hglabor.tracker.target", ImmutableMap.of("targetName", target.getName()), ChatUtils.getPlayerLocale(player)));
            }
        }
    }

    private Entity searchForCompassTarget(Player tracker) {
        for (HGPlayer hgPlayer : PlayerList.INSTANCE.getOnlinePlayers()) {
            Entity possibleTarget = Bukkit.getEntity(hgPlayer.getUUID());
            if (possibleTarget == null) continue;
            if (tracker == possibleTarget) continue;
            if (possibleTarget.getLocation().distanceSquared(tracker.getLocation()) > 30.0) {
                return possibleTarget;
            }
        }
        return null;
    }
}



