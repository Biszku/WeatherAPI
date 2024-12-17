package com.WeatherAPI;

import redis.clients.jedis.Jedis;

public class RedisRateLimiter {
    private final Jedis jedis;
    private final int MAX_REQUESTS;
    private final long WINDOW_SIZE_SECONDS;

    public RedisRateLimiter(Jedis jedis, int maxRequests, long windowSizeSeconds) {
        this.jedis = jedis;
        this.MAX_REQUESTS = maxRequests;
        this.WINDOW_SIZE_SECONDS = windowSizeSeconds;
    }

    public boolean isAllowed(String clientId) {
        String key = "rate_limiter:" + clientId;
        long currentCount = jedis.incr(key);

        if (currentCount == 1) {
            jedis.expire(key, (int) WINDOW_SIZE_SECONDS);
        }

        return currentCount <= MAX_REQUESTS;
    }
}
