package de.hglabor.plugins.hardcoregames.game.mechanics;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.UUID;

public class MooshroomCowNerf implements Listener {
    private final HashMap<UUID, Integer> cows;
    private final int MAX_SOUPS_FROM_COW;
    private final int COMBAT_MULTIPLIER;

    public MooshroomCowNerf() {
        this.cows = new HashMap<>();
        this.MAX_SOUPS_FROM_COW = HGConfig.getInteger(ConfigKeys.MOOSHROOM_COW_NERF_MAX_SOUPS_FROM_COW);
        this.COMBAT_MULTIPLIER = HGConfig.getInteger(ConfigKeys.MOOSHROOM_COW_NERF_COMBAT_MULTIPLIER);
    }

    @EventHandler
    public void onRightclickMooshroomCow(PlayerInteractEntityEvent event) {
        // the event fires for each hand, but we dont want our code to be executed twice
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (event.getRightClicked().getType() != EntityType.MUSHROOM_COW) {
            return;
        }
        MushroomCow entity = (MushroomCow) event.getRightClicked();
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.BOWL ||
           player.getInventory().getItemInOffHand().getType() == Material.BOWL) {
            UUID entityUUID = entity.getUniqueId();
            HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(player);
            int milkedSoups = cows.getOrDefault(entityUUID, 0);
            milkedSoups += hgPlayer.isInCombat() ? 1 : COMBAT_MULTIPLIER;
            cows.put(entityUUID, milkedSoups);

            if ((MAX_SOUPS_FROM_COW - milkedSoups) % 10 == 0 || milkedSoups < 5) {
                player.sendMessage(Localization.INSTANCE.getMessage("mushroomcownerf.timesLeftToMilk",
                        ImmutableMap.of("soupsLeft", String.valueOf(MAX_SOUPS_FROM_COW - milkedSoups)),
                        ChatUtils.getPlayerLocale(player)));
            }

            if (milkedSoups >= MAX_SOUPS_FROM_COW) {
                player.getWorld().spawnParticle(Particle.CRIMSON_SPORE, entity.getLocation().clone().add(0.0, 0.5, 0.0), 15, 0.1, 0.2, 0.1, 1.2);
                player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, SoundCategory.AMBIENT, 1, 1);
                entity.remove();
                entity.getWorld().spawnEntity(entity.getLocation(), EntityType.COW);
                cows.remove(entityUUID);
            }
        }
    }
}
