package org.example.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Security配置属性
 *
 * @author Lion Li
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 排除路径
     */
    private List<String> excludes = new ArrayList<>();

    /**
     * 默认构造函数，初始化默认排除路径
     */
    public SecurityProperties() {
        // 静态资源
        excludes.add("/favicon.ico");
        excludes.add("/error");
        excludes.add("/assets/**");
        excludes.add("/webjars/**");
        excludes.add("/v3/api-docs/**");
        excludes.add("/swagger-ui/**");
        excludes.add("/swagger-resources/**");
        
        // 认证相关接口
        excludes.add("/auth/login");
        excludes.add("/auth/register");
        excludes.add("/auth/captcha");
        excludes.add("/auth/logout");
        
        // 登录接口（当前项目使用的路径）
        excludes.add("/login");
        excludes.add("/register");
        excludes.add("/captcha");
        
        // 系统配置接口（部分公开）
        excludes.add("/system/config/registerEnabled");
        excludes.add("/system/config/getConfigByKey");
        
        // 字典翻译接口（公开访问）
        excludes.add("/system/dict/type/list");
        excludes.add("/system/dict/type/getDictLabel");
        excludes.add("/system/dict/type/getDictValue");
        excludes.add("/system/dict/type/getDictData");
        excludes.add("/system/dict/type/getAllDictByDictType");
        
        // 加密相关接口
        excludes.add("/encrypt/rsa/publicKey");
        
        // 健康检查
        excludes.add("/actuator/health");
        
        // Druid监控（可选，根据需要开放）
        excludes.add("/druid/**");
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}