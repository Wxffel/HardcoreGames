package de.hglabor.plugins.hardcoregames.game.phases;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class InvincibilityPhase implements GamePhase, Listener {
    protected int invincibilityTime;

    public InvincibilityPhase() {
        this.invincibilityTime = HGConfig.getInteger(ConfigKeys.INVINCIBILITY_TIME);
    }

    @Override
    public void tick(int timer) {
        final int timeLeft = invincibilityTime - timer;

        announceRemainingTime(timeLeft);

        if (timeLeft <= 0) {
            HandlerList.unregisterAll((Listener) GameStateManager.INSTANCE.getPhase());
            GameStateManager.INSTANCE.setPhase(new IngamePhase());
            ChatUtils.broadcastMessage("invincibilityPhase.timeIsUp");
        }
    }

    private void announceRemainingTime(int timeLeft) {
        if (timeLeft % 60 == 0 || timeLeft <= 5) {
            ChatUtils.broadcastMessage("invincibilityPhase.timeAnnouncement", ImmutableMap.of("timeString", TimeConverter.stringify(timeLeft)));
        }
    }

    @Override
    public PhaseType getType() {
        return PhaseType.INVINCIBILITY;
    }

    @EventHandler
    public void onPlayerReceivesDamage(EntityDamageEvent event) {
        event.setCancelled(event.getEntity() instanceof Player);
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(event.getEntity() instanceof Player);
    }
}
