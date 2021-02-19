package de.hglabor.plugins.hardcoregames.util;

import de.hglabor.plugins.hardcoregames.HardcoreGames;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public final class JedisUtils {
    private JedisUtils() {
    }

    public static JedisPoolConfig buildPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    public static void subscribe(JedisPubSub channel, String... channels) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = HardcoreGames.getJedisPool().getResource()) {
                jedis.subscribe(channel, channels);
            }
        });
    }

    public static void publish(String channel, String message) {
        try (Jedis jedis = HardcoreGames.getJedisPool().getResource()) {
            jedis.publish(channel, message);
        }
    }
}
