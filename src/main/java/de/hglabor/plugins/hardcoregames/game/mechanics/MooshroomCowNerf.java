package de.hglabor.plugins.hardcoregames.game.mechanics;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
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

    public MooshroomCowNerf() {
        this.cows = new HashMap<>();
    }

    private final int maxSoupsFromCow = HGConfig.getInteger(ConfigKeys.MOOSHROOM_COW_NERF_MAXSOUPSFROMCOW);

    @EventHandler
    public void onRightclickMooshroomCow(PlayerInteractEntityEvent event) {

        // the event fires for each hand, but we dont want our code to be executed twice
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (event.getRightClicked().getType() != EntityType.MUSHROOM_COW) return;

        MushroomCow entity = (MushroomCow) event.getRightClicked();
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() == Material.BOWL ||
                player.getInventory().getItemInOffHand().getType() == Material.BOWL) {

            UUID entityUUID = entity.getUniqueId();

            // might not work - couldn't test it because of these tokens damn
            HGPlayer hgPlayer = PlayerList.INSTANCE.getKitPlayer(player);

            int amount = 1;
            if (hgPlayer.isInCombat)
                amount += HGConfig.getInteger(ConfigKeys.MOOSHROOM_COW_NERF_SOUPSINADDITION);

            cows.put(entityUUID, cows.getOrDefault(entityUUID, 0) + amount);

            int soupsGiven = cows.get(entityUUID);

            if (soupsGiven >= maxSoupsFromCow) {
                // visual & sound
                player.getWorld().spawnParticle(Particle.CRIMSON_SPORE,
                        entity.getLocation().add(0.0, 0.5, 0.0), 15, 0.1, 0.2, 0.1, 1.2);
                player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, SoundCategory.AMBIENT, 1, 1);

                entity.remove();
                entity.getWorld().spawnEntity(entity.getLocation(), EntityType.COW);

                cows.remove(entityUUID);
            } else if ((maxSoupsFromCow - soupsGiven) % 10 == 0) {
                // original: player.sendMessage("Du kannst die §bKuh §rnoch §b" + (maxSoupsFromCow - soupsGiven) + " §rmal melken!");
                // Message: "Anzahl der noch verfügbaren Milch: "
                player.sendMessage(Localization.INSTANCE.getMessage("mushroomcownerf.timesLeftToMilk") + " " + (maxSoupsFromCow - soupsGiven));
            }
        }
    }
}
