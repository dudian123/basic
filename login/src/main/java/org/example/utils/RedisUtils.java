package org.example.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.*;

import java.time.Duration;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtils {

    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    public static <T> void setCacheObject(final String key, final T value, final Duration duration) {
        RBatch batch = CLIENT.createBatch();
        RBucketAsync<T> bucket = batch.getBucket(key);
        bucket.setAsync(value);
        bucket.expireAsync(duration);
        batch.execute();
    }

    public static <T> void setCacheObject(final String key, final T value) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        bucket.set(value);
    }

    public static <T> RBucket<T> getCacheObject(final String key) {
        return CLIENT.getBucket(key);
    }

    public static <T> void deleteObject(final String key) {
        CLIENT.getBucket(key).delete();
    }

    /**
     * 获取key的剩余存活时间（毫秒）
     */
    public static long getTimeToLive(final String key) {
        RBucket<Object> bucket = CLIENT.getBucket(key);
        return bucket.remainTimeToLive();
    }

    /**
     * 设置key的过期时间
     */
    public static boolean expire(final String key, final Duration duration) {
        RBucket<Object> bucket = CLIENT.getBucket(key);
        return bucket.expire(duration);
    }

    /**
     * 获取所有匹配的key
     *
     * @param pattern 匹配模式
     * @return key列表
     */
    public static java.util.Collection<String> keys(String pattern) {
        java.util.List<String> keyList = new java.util.ArrayList<>();
        Iterable<String> keys = CLIENT.getKeys().getKeysByPattern(pattern);
        for (String key : keys) {
            keyList.add(key);
        }
        return keyList;
    }

    /**
     * 限流
     *
     * @param key          限流key
     * @param rateType     限流类型
     * @param rate         速率
     * @param rateInterval 速率间隔
     * @param timeout      超时时间
     * @return -1 表示失败
     */
    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval, int timeout) {
        RRateLimiter rateLimiter = CLIENT.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, Duration.ofSeconds(rateInterval), Duration.ofSeconds(timeout));
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        } else {
            return -1L;
        }
    }

    /**
     * 获取客户端实例
     */
    public static RedissonClient getClient() {
        return CLIENT;
    }
}
