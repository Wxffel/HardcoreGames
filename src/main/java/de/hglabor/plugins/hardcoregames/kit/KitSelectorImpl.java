package de.hglabor.plugins.hardcoregames.kit;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.selector.KitSelector;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KitSelectorImpl extends KitSelector {
    public KitSelectorImpl() {
        super();
    }

    @EventHandler
    public void onKitSelectorClick(PlayerInteractEvent event) {
        if (event.getItem() != null && isKitSelectorItem(event.getItem())) {
            if (GameStateManager.INSTANCE.getPhase().getType().equals(PhaseType.LOBBY)) {
                openFirstPage(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String inventoryTitle = event.getView().getTitle();

        if (clickedItem == null) {
            return;
        }

        if (inventoryTitle.contains(KIT_SELECTOR_TITLE)) {
            event.setCancelled(true);
            if (nextPage(inventoryTitle, clickedItem, player)) {
                return;
            }
            if (lastPage(inventoryTitle, clickedItem, player)) {
                return;
            }
            ItemStack kitSelector = getKitSelectorInHand(player);
            AbstractKit kit = KitApi.getInstance().byItem(clickedItem);
            if (kitSelector != null && isKitSelectorItem(kitSelector) && kit != null) {
                String itemDisplayName = kitSelector.getItemMeta().getDisplayName();
                int index = Integer.parseInt(itemDisplayName.substring(itemDisplayName.length() - 1)) - 1;

                PlayerList.INSTANCE.getPlayer(player).setKit(kit, index);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                player.sendMessage(Localization.INSTANCE.getMessage("kitSelection.pickMessage",
                        ImmutableMap.of("kit", kit.getName()), ChatUtils.getPlayerLocale(player)));
                player.closeInventory();
            }
        }
    }

    private ItemStack getKitSelectorInHand(Player player) {
        for (ItemStack kitSelectorItem : kitSelectorItems) {
            if (kitSelectorItem.isSimilar(player.getInventory().getItemInMainHand())) {
                return player.getInventory().getItemInMainHand();
            } else if (kitSelectorItem.isSimilar(player.getInventory().getItemInOffHand())) {
                return player.getInventory().getItemInOffHand();
            }
        }
        return null;
    }
}
