package de.hglabor.plugins.hardcoregames.game.mechanics;

import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SoupHealing implements Listener {
    public static final List<Material> SOUP_MATERIAL = Arrays.asList(
            Material.MUSHROOM_STEW,
            Material.BEETROOT_SOUP,
            Material.RABBIT_STEW,
            Material.SUSPICIOUS_STEW
    );

    public static final HashMap<Material, Integer> SOUP_HEALING = new HashMap<>() {{
        put(Material.MUSHROOM_STEW, HGConfig.getInteger(ConfigKeys.SOUP_HEALING_MUSHROOM_STEW));
        put(Material.BEETROOT_SOUP, HGConfig.getInteger(ConfigKeys.SOUP_HEALING_BEETROOT_SOUP));
        put(Material.RABBIT_STEW, HGConfig.getInteger(ConfigKeys.SOUP_HEALING_RABBIT_STEW));
        put(Material.SUSPICIOUS_STEW, HGConfig.getInteger(ConfigKeys.SOUP_HEALING_SUSPICIOUS_STEW));
    }};

    public static final HashMap<Material, Integer> RESTORED_FOOD = new HashMap<>() {{
        put(Material.MUSHROOM_STEW, 6);
        put(Material.BEETROOT_SOUP, 6);
        put(Material.RABBIT_STEW, 10);
    }};

    public static final HashMap<Material, Float> RESTORED_SATURATION = new HashMap<>() {{
        put(Material.MUSHROOM_STEW, 7.2f);
        put(Material.BEETROOT_SOUP, 7.2f);
        put(Material.RABBIT_STEW, 12f);
    }};

    @EventHandler
    public void onRightClickSoup(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player p = event.getPlayer();

        if (p.getHealth() >= p.getHealthScale() && p.getFoodLevel() >= 20) return;

        ItemStack interactItem = p.getInventory().getItem(event.getHand());

        if (SOUP_MATERIAL.contains(interactItem.getType())) {

            Material interactItemType = interactItem.getType();

            int healing = SOUP_HEALING.get(interactItemType);
            if (interactItem.hasItemMeta() &&
                    event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(KitMetaData.SPIT_SOUP.getKey()))
                healing = HGConfig.getInteger(ConfigKeys.SOUP_HEALING_SPIT_SOUP);

            // 0 means disabled
            if (healing == 0) return;

            boolean ifConsume = false;

            if (p.getHealth() < p.getHealthScale()) {
                p.setHealth(Math.min(p.getHealth() + healing, p.getHealthScale()));
                ifConsume = true;
            } else if (p.getFoodLevel() < 20) {
                p.setFoodLevel(Math.min(p.getFoodLevel() + RESTORED_FOOD.get(interactItemType), 20));
                p.setSaturation(Math.min(p.getSaturation() + RESTORED_SATURATION.get(interactItemType), p.getFoodLevel()));
                ifConsume = true;
            }
            if (ifConsume) interactItem.setType(Material.BOWL);
        }
    }
}
