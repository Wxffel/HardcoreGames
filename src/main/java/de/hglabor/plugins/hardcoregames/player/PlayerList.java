package de.hglabor.plugins.hardcoregames.player;

import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.supplier.KitPlayerSupplier;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public final class PlayerList implements KitPlayerSupplier {
    private static final PlayerList instance = new PlayerList();
    private final Map<UUID, HGPlayer> players;

    private PlayerList() {
        this.players = new HashMap<>();
    }

    public HGPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public HGPlayer getPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, HGPlayer::new);
    }

    public void add(HGPlayer ffaPlayer) {
        players.put(ffaPlayer.getUUID(), ffaPlayer);
    }

    public void remove(Player player) {
        remove(player.getUniqueId());
    }

    public void remove(UUID uuid) {
        players.remove(uuid);
    }

    @Override
    public KitPlayer getKitPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public List<HGPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public List<HGPlayer> getPlayersInKitSelection() {
        return players.values().stream().filter(HGPlayer::isWaiting).collect(Collectors.toList());
    }

    public List<HGPlayer> getWaitingPlayers() {
        return players.values().stream().filter(HGPlayer::isWaiting).collect(Collectors.toList());
    }

    public static PlayerList getInstance() {
        return instance;
    }
}
