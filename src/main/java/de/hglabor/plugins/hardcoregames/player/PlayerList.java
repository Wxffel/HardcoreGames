package de.hglabor.plugins.hardcoregames.player;

import de.hglabor.plugins.hardcoregames.util.Logger;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.supplier.KitPlayerSupplier;
import de.hglabor.utils.noriskutils.queue.hg.HGQueuePlayerInfo;
import de.hglabor.utils.noriskutils.staffmode.StaffPlayer;
import de.hglabor.utils.noriskutils.staffmode.StaffPlayerSupplier;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public final class PlayerList implements KitPlayerSupplier, StaffPlayerSupplier {
    public static final PlayerList INSTANCE = new PlayerList();
    private final Map<UUID, HGPlayer> players;

    private PlayerList() {
        this.players = new HashMap<>();
    }

    public HGPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public HGPlayer getPlayer(HGQueuePlayerInfo hgQueueJoinInfo) {
        return players.computeIfAbsent(UUID.fromString(hgQueueJoinInfo.getUuid()), id -> new HGPlayer(id, hgQueueJoinInfo.getName()));
    }

    public HGPlayer getPlayer(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), uuid -> new HGPlayer(uuid, player.getName()));
    }

    public void remove(UUID uuid) {
        Logger.debug(String.format("%s was removed", uuid));
        players.remove(uuid);
    }

    public void remove(HGPlayer player) {
        remove(player.getUUID());
    }

    @Override
    public KitPlayer getKitPlayer(Player player) {
        return getPlayer(player);
    }

    public List<HGPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public List<HGPlayer> getAlivePlayers() {
        return players.values().stream().filter(hgPlayer -> hgPlayer.getStatus().equals(PlayerStatus.ALIVE) || hgPlayer.getStatus().equals(PlayerStatus.OFFLINE)).collect(Collectors.toList());
    }

    public List<HGPlayer> getOnlinePlayers() {
        return getAlivePlayers().stream().filter(hgPlayer -> hgPlayer.getStatus().equals(PlayerStatus.ALIVE)).collect(Collectors.toList());
    }

    public List<HGPlayer> getWaitingPlayers() {
        return players.values().stream().filter(player -> player.getStatus().equals(PlayerStatus.WAITING) || player.getStatus().equals(PlayerStatus.QUEUE)).collect(Collectors.toList());
    }

    public List<HGPlayer> getQueueingPlayers() {
        return players.values().stream().filter(player -> player.getStatus().equals(PlayerStatus.QUEUE)).collect(Collectors.toList());
    }

    @Override
    public StaffPlayer getStaffPlayer(Player player) {
        return getPlayer(player);
    }

    @Override
    public Player getRandomActivePlayer() {
        List<HGPlayer> collect = getPlayers().stream().filter(hgPlayer -> hgPlayer.getPlayer() != null && (hgPlayer.getStatus().equals(PlayerStatus.WAITING) || hgPlayer.getStatus().equals(PlayerStatus.ALIVE))).collect(Collectors.toList());
        return collect.get(new Random().nextInt(collect.size())).getPlayer();
    }
}
