package org.example.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import org.example.config.satoken.TenantSaTokenDao;
import org.example.service.SaPermissionImpl;
import org.example.service.ISysRoleService;
import org.example.service.ISysMenuService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Sa-Token 配置类
 *
 * @author example
 */
@Configuration
public class SaTokenConfig {

    /**
     * 获取 StpLogic 的 jwt 实现
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        // Sa-Token 整合 jwt (简单模式)
        return new StpLogicJwtForSimple();
    }

    /**
     * 权限接口实现(使用bean注入方便用户替换)
     */
    @Bean
    public StpInterface stpInterface(ISysRoleService roleService, ISysMenuService menuService) {
        return new SaPermissionImpl(roleService, menuService);
    }

    /**
     * 多租户 Sa-Token 持久层实现
     */
    @Bean
    @Primary
    @ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
    public SaTokenDao saTokenDao() {
        return new TenantSaTokenDao();
    }

}