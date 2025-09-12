package org.example.config.cache;

import lombok.extern.slf4j.Slf4j;
import org.example.utils.TenantHelper;
import org.redisson.api.NameMapper;

/**
 * 多租户Redis缓存key前缀处理
 * 用于Redisson客户端的key前缀处理
 *
 * @author example
 */
@Slf4j
public class TenantKeyPrefixHandler implements NameMapper {

    private final String keyPrefix;

    public TenantKeyPrefixHandler(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    /**
     * 增加前缀
     */
    @Override
    public String map(String name) {
        if (TenantHelper.isEnable()) {
            String tenantId = TenantHelper.getTenantId();
            if (tenantId != null) {
                return keyPrefix + tenantId + ":" + name;
            }
        }
        return keyPrefix + name;
    }

    /**
     * 去除前缀
     */
    @Override
    public String unmap(String name) {
        if (name.startsWith(keyPrefix)) {
            String nameWithoutPrefix = name.substring(keyPrefix.length());
            if (TenantHelper.isEnable()) {
                String tenantId = TenantHelper.getTenantId();
                if (tenantId != null && nameWithoutPrefix.startsWith(tenantId + ":")) {
                    return nameWithoutPrefix.substring((tenantId + ":").length());
                }
            }
            return nameWithoutPrefix;
        }
        return name;
    }

}