package de.hglabor.plugins.hardcoregames.queue;

public class HGQueuePlayerInfo {
    private final String uuid;
    private String name;
    private int port;

    public HGQueuePlayerInfo(String uuid, String name, int port) {
        this.uuid = uuid;
        this.name = name;
        this.port = port;
    }

    public HGQueuePlayerInfo(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }
}
