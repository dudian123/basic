package org.example.config.satoken;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.GlobalConstants;
import org.example.utils.SpringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sa-Token持久层接口 多租户实现
 * 参照RuoYi-Vue-Plus原版实现，使用固定的global:前缀避免循环调用
 * 使用懒加载方式获取RedissonClient，避免Spring容器初始化时的循环依赖
 *
 * @author ruoyi
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class TenantSaTokenDao implements SaTokenDao {
    
    /**
     * 懒加载获取RedissonClient，避免循环依赖
     */
    private RedissonClient getRedissonClient() {
        try {
            return SpringUtils.getBean(RedissonClient.class);
        } catch (Exception e) {
            log.warn("获取RedissonClient失败，Sa-Token将使用内存存储: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return null;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        Object value = bucket.get();
        return value != null ? value.toString() : null;
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return;
        }
        RBucket<String> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            bucket.set(value);
        } else {
            bucket.set(value, Duration.ofSeconds(timeout));
        }
    }

    /**
     * 修修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        bucket.delete();
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return NOT_VALUE_EXPIRE;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        long timeToLive = bucket.remainTimeToLive();
        return timeToLive < 0 ? timeToLive : timeToLive / 1000;
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return;
        }
        // 判断是否想要设置为永久
        if (timeout == NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if (expire == NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        bucket.expire(Duration.ofSeconds(timeout));
    }

    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return null;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        return bucket.get();
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            bucket.set(object);
        } else {
            bucket.set(object, Duration.ofSeconds(timeout));
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if (expire == NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        bucket.delete();
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return NOT_VALUE_EXPIRE;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        long timeToLive = bucket.remainTimeToLive();
        return timeToLive < 0 ? timeToLive : timeToLive / 1000;
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return;
        }
        // 判断是否想要设置为永久
        if (timeout == NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if (expire == NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        RBucket<Object> bucket = client.getBucket(GlobalConstants.GLOBAL_REDIS_KEY + key);
        bucket.expire(Duration.ofSeconds(timeout));
    }

    /**
     * 搜索数据
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        RedissonClient client = getRedissonClient();
        if (client == null) {
            return new ArrayList<>();
        }
        Iterable<String> keys = client.getKeys().getKeysByPattern(GlobalConstants.GLOBAL_REDIS_KEY + prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<>();
        for (String key : keys) {
            // 移除global:前缀
            if (key.startsWith(GlobalConstants.GLOBAL_REDIS_KEY)) {
                list.add(key.substring(GlobalConstants.GLOBAL_REDIS_KEY.length()));
            }
        }
        return SaFoxUtil.searchList(list, start, size, sortType);
    }

}