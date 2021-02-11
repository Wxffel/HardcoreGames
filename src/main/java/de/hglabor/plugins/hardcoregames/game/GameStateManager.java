package de.hglabor.plugins.hardcoregames.game;

import de.hglabor.plugins.hardcoregames.HardcoreGames;
import de.hglabor.plugins.hardcoregames.config.HGConfig;
import de.hglabor.plugins.hardcoregames.game.phases.GamePhase;
import de.hglabor.plugins.hardcoregames.game.phases.LobbyPhase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class GameStateManager {
    public static final GameStateManager INSTANCE = new GameStateManager();
    private final AtomicInteger timer;
    private GamePhase phase;

    private GameStateManager() {
        this.timer = new AtomicInteger();
        this.phase = new LobbyPhase();
    }

    public void run() {
        Bukkit.getPluginManager().registerEvents((Listener) phase, HardcoreGames.getPlugin());
        Optional<World> world = Optional.ofNullable(Bukkit.getWorld("world"));
        world.ifPresent(HGConfig::lobbyWorldSettings);
        Bukkit.getScheduler().runTaskTimer(HardcoreGames.getPlugin(), () -> phase.tick(timer.getAndIncrement()), 0, 20L);
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public void resetTimer() {
        timer.set(0);
    }

    public int getTimer() {
        return timer.get();
    }
}
