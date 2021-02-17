package de.hglabor.plugins.hardcoregames.game.phase;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.config.ConfigKeys;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.GamePhase;
import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.game.PhaseType;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.hardcoregames.player.PlayerStatus;
import de.hglabor.plugins.hardcoregames.queue.HGQueueInfo;
import de.hglabor.plugins.hardcoregames.util.ChannelIdentifier;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import de.hglabor.utils.noriskutils.PotionUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class LobbyPhase extends GamePhase {
    public final static byte[] HG_QUEUE_INFO_BYTES = HardcoreGames.GSON.toJson(new HGQueueInfo(Bukkit.getPort()), HGQueueInfo.class).getBytes(StandardCharsets.UTF_8);
    protected final ItemStack queueItem;
    protected int forceStartTime;
    protected int requiredPlayerAmount;
    protected int timeLeft;
    protected boolean isStarting;


    public LobbyPhase() {
        super(HGConfig.getInteger(ConfigKeys.LOBBY_WAITING_TIME));
        this.forceStartTime = HGConfig.getInteger(ConfigKeys.COMMAND_FORCESTART_TIME);
        this.requiredPlayerAmount = HGConfig.getInteger(ConfigKeys.LOBBY_PLAYERS_NEEDED);
        this.queueItem = new ItemBuilder(Material.EMERALD).setName("Queue").build();
    }

    @Override
    protected void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Optional<World> world = Optional.ofNullable(Bukkit.getWorld("world"));
        world.ifPresent(HGConfig::lobbyWorldSettings);
    }

    @Override
    protected void tick(int timer) {
        if (playerList.getWaitingPlayers().size() >= requiredPlayerAmount) {
            timeLeft = maxPhaseTime - timer;
            announceRemainingTime(timeLeft);

            if (timeLeft == forceStartTime) {
                isStarting = true;
                for (HGPlayer waitingPlayer : playerList.getWaitingPlayers()) {
                    waitingPlayer.getBukkitPlayer().ifPresent(player -> {
                        PotionUtils.paralysePlayer(player);
                        player.getInventory().removeItem(queueItem);
                    });
                    waitingPlayer.teleportToSafeSpawn();
                }
            }

            if (timeLeft <= 0) {
                GameStateManager.INSTANCE.resetTimer();
                if (PlayerList.INSTANCE.getWaitingPlayers().size() >= requiredPlayerAmount) {
                    //TODO SOUNDS
                    this.startNextPhase();
                    ChatUtils.broadcastMessage("lobbyPhase.gameStarts");
                } else {
                    ChatUtils.broadcastMessage("lobbyPhase.notEnoughPlayers", ImmutableMap.of("requiredPlayers", String.valueOf(requiredPlayerAmount)));
                    isStarting = false;
                    playerList.getWaitingPlayers().forEach(waitingPlayer -> waitingPlayer.getBukkitPlayer().ifPresent(player -> {
                        player.getInventory().addItem(queueItem);
                        PotionUtils.removePotionEffects(player);
                    }));
                }
            }
        } else {
            isStarting = false;
            GameStateManager.INSTANCE.resetTimer();
        }
    }

    private void announceRemainingTime(int timeLeft) {
        if (timeLeft % 30 == 0 || timeLeft <= 5 || timeLeft == 15 || timeLeft == 10) {
            ChatUtils.broadcastMessage("lobbyPhase.timeAnnouncement", ImmutableMap.of("timeString", TimeConverter.stringify(timeLeft)));
        }
    }

    @Override
    public PhaseType getType() {
        return PhaseType.LOBBY;
    }

    @Override
    public int getRawTime() {
        int rawTime = super.getRawTime();
        return rawTime == 0 ? maxPhaseTime - rawTime : timeLeft;
    }

    public boolean isStarting() {
        return isStarting;
    }

    public void setStarting(boolean isStarting) {
        this.isStarting = isStarting;
    }

    @Override
    protected String getTimeString(int timer) {
        return TimeConverter.stringify(getRawTime());
    }

    @Override
    public int getCurrentParticipants() {
        return playerList.getWaitingPlayers().size();
    }

    @Override
    public int getMaxParticipants() {
        return getCurrentParticipants();
    }

    @Override
    protected GamePhase getNextPhase() {
        return new InvincibilityPhase();
    }

    public void setPlayerLobbyReady(Player player) {
        player.getInventory().clear();
        player.setHealth(20);
        player.setFireTicks(0);
        player.setFlying(false);
        player.setTotalExperience(0);
        player.setExp(0);
        player.setAllowFlight(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        PotionUtils.removePotionEffects(player);
        KitApi.getInstance().getKitSelector().getKitSelectorItems().forEach(item -> player.getInventory().addItem(item));
        HGPlayer hgPlayer = playerList.getPlayer(player);
        hgPlayer.setStatus(PlayerStatus.WAITING);
        hgPlayer.teleportToSafeSpawn();
        if (isStarting) {
            PotionUtils.paralysePlayer(player);
        } else {
            player.getInventory().addItem(queueItem);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        setPlayerLobbyReady(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        HGPlayer hgPlayer = playerList.getPlayer(event.getPlayer());
        if (!hgPlayer.getStatus().equals(PlayerStatus.QUEUE)) {
            playerList.remove(hgPlayer);
        }
    }

    @EventHandler
    public void onRightClickQueueItem(PlayerInteractEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getItem();
        if (item != null && item.isSimilar(queueItem)) {
            Player player = event.getPlayer();
            HGPlayer hgPlayer = playerList.getPlayer(player);
            player.sendPluginMessage(HardcoreGames.getPlugin(), ChannelIdentifier.HG_QUEUE, HG_QUEUE_INFO_BYTES);
            hgPlayer.setStatus(PlayerStatus.QUEUE);
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        if (isStarting) event.setCancelled(true);
    }

    @EventHandler
    private void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChangeEvent(FoodLevelChangeEvent event) { event.setCancelled(true); }

    @EventHandler
    private void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreakEvent(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerPickupExperienceEvent(PlayerPickupExperienceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerAttemptPickupItemEvent(PlayerAttemptPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
