package de.hglabor.plugins.hardcoregames.game.phases;

import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;

public class IngamePhase implements GamePhase {
    protected int maxPlayTime;

    public IngamePhase() {
        this.maxPlayTime = HGConfig.getInteger(ConfigKeys.INGAME_MAX_PLAYTIME);
    }

    @Override
    public void tick(int timer) {
        if (timer >= maxPlayTime) {
            //TODO END
        } else {

        }
    }

    @Override
    public PhaseType getType() {
        return PhaseType.INGAME;
    }
}
