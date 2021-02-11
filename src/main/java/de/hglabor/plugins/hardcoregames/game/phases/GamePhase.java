package de.hglabor.plugins.hardcoregames.game.phases;

public interface GamePhase {
    void tick(int timer);

    PhaseType getType();
}
