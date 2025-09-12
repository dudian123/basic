package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.SysConfigMapper;
import org.example.service.ISysConfigService;
import org.example.domain.vo.SysConfigVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysConfigServiceImpl implements ISysConfigService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SysConfigMapper configMapper;

    /**
     * 系统配置缓存前缀
     */
    private static final String CONFIG_CACHE_KEY = "sys_config:";
    private static final long CACHE_EXPIRE_TIME = 24; // 24小时过期

    /**
     * 注册功能配置键
     */
    private static final String REGISTER_ENABLED_KEY = "sys.account.registerUser";

    /**
     * 默认配置
     */
    private static final Map<String, String> DEFAULT_CONFIG = new HashMap<>();

    static {
        // 初始化默认配置
        DEFAULT_CONFIG.put(REGISTER_ENABLED_KEY, "true"); // 默认开启注册功能
        DEFAULT_CONFIG.put("sys.user.initPassword", "123456"); // 默认初始密码
        DEFAULT_CONFIG.put("sys.index.skinName", "skin-blue"); // 默认皮肤
        DEFAULT_CONFIG.put("sys.user.captchaEnabled", "true"); // 默认开启验证码
    }

    @Override
    public String selectConfigByKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }
        
        // 先从缓存获取
        String cacheKey = CONFIG_CACHE_KEY + configKey;
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            return cachedValue.toString();
        }
        
        // 缓存未命中，从数据库获取
        String configValue = configMapper.selectConfigByKey(configKey);
        
        // 如果数据库中没有，使用默认值
        if (configValue == null) {
            configValue = getDefaultConfigValue(configKey);
        }
        
        // 存入缓存
        if (configValue != null) {
            redisTemplate.opsForValue().set(cacheKey, configValue, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
        }
        
        return configValue;
    }

    /**
     * 获取默认配置值
     */
    private String getDefaultConfigValue(String configKey) {
        return DEFAULT_CONFIG.get(configKey);
    }

    @Override
    public boolean selectRegisterEnabled() {
        String configValue = selectConfigByKey(REGISTER_ENABLED_KEY);
        if (configValue == null) {
            return true; // 默认开启注册功能
        }
        
        return "true".equalsIgnoreCase(configValue.trim()) || "1".equals(configValue.trim());
    }

    @Override
    public boolean setConfig(String configKey, String configValue) {
        if (!StringUtils.hasText(configKey)) {
            return false;
        }
        
        try {
            // 更新数据库
            int result = configMapper.updateConfig(configKey, configValue);
            if (result > 0) {
                // 更新缓存
                String cacheKey = CONFIG_CACHE_KEY + configKey;
                redisTemplate.opsForValue().set(cacheKey, configValue, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
                log.info("配置更新成功: {} = {}", configKey, configValue);
                return true;
            } else {
                log.warn("配置更新失败，未找到配置项: {}", configKey);
                return false;
            }
        } catch (Exception e) {
            log.error("配置更新失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @PostConstruct
    public void initSystemConfig() {
        log.info("开始初始化系统配置...");
        
        try {
            // 初始化默认配置
            initConfigIfNotExists("sys.account.registerUser", "true", "Register Function Switch", "Whether to enable user registration function");
            initConfigIfNotExists("sys.user.initPassword", "123456", "User Management - Initial Password", "Initial password 123456");
            initConfigIfNotExists("sys.index.skinName", "skin-blue", "Main Frame - Default Skin Style Name", "Default skin style");
            initConfigIfNotExists("sys.user.captchaEnabled", "true", "Captcha Function Switch", "Whether to enable captcha function");
            
            log.info("系统配置初始化完成");
        } catch (Exception e) {
            log.error("系统配置初始化失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 如果配置不存在则初始化
     */
    private void initConfigIfNotExists(String configKey, String configValue, String configName, String remark) {
        try {
            if (configMapper.checkConfigExists(configKey) == 0) {
                configMapper.insertConfig(configKey, configValue, configName, remark);
                log.info("初始化配置: {} = {}", configKey, configValue);
            }
        } catch (Exception e) {
            log.error("初始化配置失败: {} = {}, 错误: {}", configKey, configValue, e.getMessage());
        }
    }

    /**
     * 清除配置缓存
     *
     * @param configKey 配置键
     */
    public void clearConfigCache(String configKey) {
        try {
            String cacheKey = CONFIG_CACHE_KEY + configKey;
            redisTemplate.delete(cacheKey);
            log.info("清除配置缓存: {}", configKey);
        } catch (Exception e) {
            log.error("清除配置缓存失败: {}, 错误: {}", configKey, e.getMessage(), e);
        }
    }

    /**
     * 清除所有配置缓存
     */
    public void clearAllConfigCache() {
        try {
            redisTemplate.delete(redisTemplate.keys(CONFIG_CACHE_KEY + "*"));
            log.info("清除所有配置缓存");
        } catch (Exception e) {
            log.error("清除所有配置缓存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<SysConfigVo> selectConfigList() {
        try {
            List<Map<String, Object>> configList = configMapper.selectConfigList();
            return configList.stream().map(this::convertToSysConfigVo).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询参数配置列表失败: {}", e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 转换Map为SysConfigVo
     */
    private SysConfigVo convertToSysConfigVo(Map<String, Object> map) {
        SysConfigVo vo = new SysConfigVo();
        vo.setConfigId(map.get("config_id") != null ? Long.valueOf(map.get("config_id").toString()) : null);
        vo.setTenantId(map.get("tenant_id") != null ? map.get("tenant_id").toString() : null);
        vo.setConfigName(map.get("config_name") != null ? map.get("config_name").toString() : null);
        vo.setConfigKey(map.get("config_key") != null ? map.get("config_key").toString() : null);
        vo.setConfigValue(map.get("config_value") != null ? map.get("config_value").toString() : null);
        vo.setConfigType(map.get("config_type") != null ? map.get("config_type").toString() : null);
        vo.setCreateTime(map.get("create_time") != null ? (LocalDateTime) map.get("create_time") : null);
        vo.setRemark(map.get("remark") != null ? map.get("remark").toString() : null);
        return vo;
    }
}