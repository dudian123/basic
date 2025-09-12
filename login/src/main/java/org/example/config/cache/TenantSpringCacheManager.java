package org.example.config.cache;

import lombok.extern.slf4j.Slf4j;
import org.example.utils.TenantHelper;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.Nullable;

/**
 * 多租户缓存管理器
 * 重写缓存名称处理，支持多租户
 *
 * @author example
 */
@Slf4j
public class TenantSpringCacheManager extends RedisCacheManager {

    public TenantSpringCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultConfiguration) {
        super(cacheWriter, defaultConfiguration);
    }

    @Override
    public Cache getCache(String name) {
        if (TenantHelper.isEnable()) {
            String tenantId = TenantHelper.getTenantId();
            if (tenantId != null) {
                // 为缓存名称添加租户前缀
                name = tenantId + ":" + name;
            }
        }
        return super.getCache(name);
    }

    @Override
    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        if (TenantHelper.isEnable()) {
            String tenantId = TenantHelper.getTenantId();
            if (tenantId != null && !name.startsWith(tenantId + ":")) {
                // 为缓存名称添加租户前缀
                name = tenantId + ":" + name;
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }

}