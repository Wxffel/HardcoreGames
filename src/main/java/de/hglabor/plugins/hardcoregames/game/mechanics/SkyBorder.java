package de.hglabor.plugins.hardcoregames.game.mechanics;

import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;

public class SkyBorder {
    private final int damage;

    public SkyBorder(int damage) {
        this.damage = damage;
    }

    public void tick() {
        for (HGPlayer hgPlayer : PlayerList.INSTANCE.getOnlinePlayers()) {
            hgPlayer.getBukkitPlayer().ifPresent(player -> {
                if (player.getLocation().getY() >= player.getWorld().getMaxHeight()) {
                    player.damage(damage);
                }
            });
        }
    }
}
