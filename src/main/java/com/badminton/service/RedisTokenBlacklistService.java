package com.badminton.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final ConcurrentHashMap<String, Long> localBlacklist = new ConcurrentHashMap<>();
    
    // Circuit breaker variables
    private volatile boolean redisAvailable = true;
    private volatile long lastRedisRetryTime = 0L;
    private static final long REDIS_RETRY_INTERVAL_MS = 60000L; // Retry connection once every 60 seconds

    public RedisTokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private void cleanUpExpiredTokens() {
        long now = System.currentTimeMillis();
        localBlacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }

    private boolean checkRedisAvailability() {
        if (redisAvailable) {
            return true;
        }
        long now = System.currentTimeMillis();
        if (now - lastRedisRetryTime > REDIS_RETRY_INTERVAL_MS) {
            try {
                // Perform a lightweight probe request to check Redis connection
                redisTemplate.hasKey("probe-connection-test-key");
                redisAvailable = true;
                System.out.println("Redis is back online. Resuming Redis operations.");
                return true;
            } catch (Exception e) {
                lastRedisRetryTime = now; // reset the retry interval
                System.err.println("Redis connection retry failed (Redis is still down): " + e.getMessage());
            }
        }
        return false;
    }

    public void addToBlacklist(String token, long remainingTimeMillis) {
        if (token == null) {
            return;
        }

        long expiryTime = System.currentTimeMillis() + remainingTimeMillis;

        // Store in local memory cache first as a fallback/immediate check
        if (remainingTimeMillis > 0) {
            localBlacklist.put(token, expiryTime);
        }

        if (checkRedisAvailability()) {
            try {
                if (remainingTimeMillis > 0) {
                    redisTemplate.opsForValue().set(token, "blacklisted", remainingTimeMillis, TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                redisAvailable = false;
                lastRedisRetryTime = System.currentTimeMillis();
                System.err.println("Warning: Failed to add token to Redis blacklist (Redis marked as DOWN): " + e.getMessage());
            }
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null) {
            return false;
        }

        cleanUpExpiredTokens();

        // 1. Check in-memory blacklist first for instant validation
        Long expiry = localBlacklist.get(token);
        if (expiry != null) {
            if (expiry > System.currentTimeMillis()) {
                return true;
            } else {
                localBlacklist.remove(token);
            }
        }

        // 2. Check Redis blacklist if available
        if (checkRedisAvailability()) {
            try {
                return Boolean.TRUE.equals(redisTemplate.hasKey(token));
            } catch (Exception e) {
                redisAvailable = false;
                lastRedisRetryTime = System.currentTimeMillis();
                System.err.println("Warning: Failed to check Redis blacklist (Redis marked as DOWN): " + e.getMessage());
            }
        }

        return false;
    }
}
