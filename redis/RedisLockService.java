package org.rushandler.rushandler.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
public class RedisLockService {

    private static final Duration DEFAULT_LOCK_TTL = Duration.ofMinutes(3); // 180 seconds
    private static final String RELEASE_LOCK_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> releaseLockScript;

    public RedisLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.releaseLockScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA, Long.class);
    }

    public LockResult acquireLock(Long eventId, Long seatId) {
        return acquireLock(eventId, seatId, DEFAULT_LOCK_TTL);
    }

    public LockResult acquireLock(Long eventId, Long seatId, Duration ttl) {
        String key = buildLockKey(eventId, seatId);
        String token = UUID.randomUUID().toString();

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, token, ttl);

        if (Boolean.TRUE.equals(acquired)) {
            return LockResult.success(token);
        }
        return LockResult.failure();
    }

    public boolean releaseLock(Long eventId, Long seatId, String token) {
        if (token == null) {
            return false;
        }

        String key = buildLockKey(eventId, seatId);
        Long result = redisTemplate.execute(
                releaseLockScript,
                Collections.singletonList(key),
                token
        );

        return Long.valueOf(1L).equals(result);
    }

    public String buildLockKey(Long eventId, Long seatId) {
        return "seat:" + eventId + ":" + seatId;
    }

    public boolean isLocked(Long eventId, Long seatId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildLockKey(eventId, seatId)));
    }

    public String getLockValue(Long eventId, Long seatId) {
        return redisTemplate.opsForValue().get(buildLockKey(eventId, seatId));
    }

    // Preserved for backward compatibility
    public boolean acquireLock(Long eventId, Long seatId, String userId) {
        LockResult result = acquireLock(eventId, seatId);
        return result.isAcquired();
    }
}