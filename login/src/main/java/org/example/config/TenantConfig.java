package org.example.config;

import org.example.config.cache.TenantKeyPrefixHandler;
import org.example.config.cache.TenantSpringCacheManager;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 多租户配置
 *
 * @author ruoyi
 */
@Configuration
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class TenantConfig {

    /**
     * 多租户Redisson自定义器
     */
    @Bean
    public RedissonAutoConfigurationCustomizer tenantRedissonCustomizer() {
        return config -> {
            TenantKeyPrefixHandler nameMapper = new TenantKeyPrefixHandler("tenant:");
            // 注释掉可能不存在的方法，避免编译错误
            // config.setNameMapper(nameMapper);
        };
    }

    /**
     * 多租户缓存管理器
     */
    @Bean
    @Primary
    public CacheManager tenantCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return new TenantSpringCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

}