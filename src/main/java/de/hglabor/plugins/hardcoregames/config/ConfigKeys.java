package de.hglabor.plugins.hardcoregames.config;

public interface ConfigKeys {
    String LOBBY = "lobby";
    String LOBBY_PLAYERS_NEEDED = LOBBY + "." + "playersToStart";
    String LOBBY_WAITING_TIME = LOBBY + "." + "timeToWait";

    String INVINCIBILITY = "invincibility";
    String INVINCIBILITY_TIME = INVINCIBILITY + "." + "time";

    String INGAME = "ingame";
    String INGAME_MAX_PLAYTIME = INGAME + "." + "maxPlayTime";

    String PLAYER = "player";
    String PLAYER_OFFLINE_TIME = PLAYER + "." + "offlineTime";
}
