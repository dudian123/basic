package org.example.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 多租户配置属性
 *
 * @author ruoyi
 */
@Data
@Component
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    /**
     * 是否启用多租户功能
     */
    private Boolean enable = false;

    /**
     * 需要排除的表（这些表不会自动添加租户条件）
     */
    private List<String> excludeTables;

}