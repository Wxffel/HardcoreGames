package de.hglabor.plugins.hardcoregames.queue;

public class HGInfo {
    private int maxPlayers;
    private int onlinePlayers;
    private int timeInSeconds;
    private String gameState;
    private String serverName;
    private int serverPort;

    public HGInfo(int maxPlayers, int onlinePlayers, int timeInSeconds, String gameState, int serverPort) {
        this.maxPlayers = maxPlayers;
        this.onlinePlayers = onlinePlayers;
        this.timeInSeconds = timeInSeconds;
        this.gameState = gameState;
        this.serverPort = serverPort;
    }

    public int getServerPort() { return serverPort; }

    public void setServerPort(int serverPort) { this.serverPort = serverPort; }

    public String getServerName() { return serverName; }

    public void setServerName(String serverName) { this.serverName = serverName; }

    public String getGameState() { return gameState; }

    public void setGameState(String gameState) { this.gameState = gameState; }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getTimeInSeconds() { return timeInSeconds; }

    public void setTimeInSeconds(int timeInSeconds) { this.timeInSeconds = timeInSeconds; }
}
