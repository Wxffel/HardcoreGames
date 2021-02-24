package de.hglabor.plugins.hardcoregames.config;

public interface ConfigKeys {
    String LOBBY = "lobby";
    String LOBBY_PLAYERS_NEEDED = LOBBY + "." + "playersToStart";
    String LOBBY_WAITING_TIME = LOBBY + "." + "timeToWait";

    String INVINCIBILITY = "invincibility";
    String INVINCIBILITY_TIME = INVINCIBILITY + "." + "time";

    String INGAME = "ingame";
    String INGAME_MAX_PLAYTIME = INGAME + "." + "maxPlayTime";


    String END = "end";
    String END_RESTART_AFTER = END + "." + "restartAfter";

    String PLAYER = "player";
    String PLAYER_OFFLINE_TIME = PLAYER + "." + "offlineTime";

    String COMMAND = "command";
    String COMMAND_FORCESTART = COMMAND + "." + "forcestart";
    String COMMAND_FORCESTART_TIME = COMMAND_FORCESTART + "." + "time";

    String FEAST = "feast";
    String FEAST_LATEST_APPEARANCE = FEAST + "." + "latestAppearance";
    String FEAST_TIME_TILL_SPAWN = FEAST + "." + "timeTillSpawn";
    String FEAST_EARLIEST_APPEARANCE = FEAST + "." + "earliestAppearance";

    String BORDER = "border";
    String SKY_BORDER = "skyborder";
    String SKY_BORDER_DAMAGE = BORDER + "." + SKY_BORDER + "." + "damage";

    String DEBUG = "debug";
    String DEBUG_IS_ENABLED = DEBUG + "." + "enabled";

    String REDIS = "redis";
    String REDIS_PW = REDIS + "." + "password";

    String SERVER = "server";
    String SERVER_PING = SERVER + "." + "ping";

    String MECHANICS = "mechanics";
    String SWORD_DAMAGE_NERF = MECHANICS + "." + "sworddamagemultiplier";
    String OTHER_TOOLS_DAMAGE_NERF = MECHANICS + "." + "othertoolsdamagemultiplier";

    String SOUP_HEALING = MECHANICS + "." + "souphealing";
    String SOUP_HEALING_MUSHROOM_STEW = SOUP_HEALING + "." + "mushroomstew";
    String SOUP_HEALING_BEETROOT_SOUP = SOUP_HEALING + "." + "beetroot_soup";
    String SOUP_HEALING_RABBIT_STEW = SOUP_HEALING + "." + "rabbitstew";
    String SOUP_HEALING_SUSPICIOUS_STEW = SOUP_HEALING + "." + "suspiciousstew";
    String SOUP_HEALING_SPIT_SOUP = SOUP_HEALING + "." + "spitsoup";
}
