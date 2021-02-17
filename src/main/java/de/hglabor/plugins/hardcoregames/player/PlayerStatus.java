package de.hglabor.plugins.hardcoregames.player;

public enum PlayerStatus {

    WAITING,

    ALIVE,

    /**
     * dead, excluded
     */
    ELIMINATED,

    SPECTATOR,

    /**
     * disconnect may or may not come back
     */
    OFFLINE,

    QUEUE
}
