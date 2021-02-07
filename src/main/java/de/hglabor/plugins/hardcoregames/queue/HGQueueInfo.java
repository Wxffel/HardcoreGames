package de.hglabor.plugins.hardcoregames.queue;

public class HGQueueInfo {
    private int serverPort;
    private String serverName;

    public HGQueueInfo(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
