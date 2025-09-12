package org.example.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.example.config.handler.PlusTenantLineHandler;
import org.example.config.properties.TenantProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

/**
 * MyBatis-Plus 配置类
 *
 * @author example
 */
@Configuration
@RequiredArgsConstructor
public class MybatisPlusConfig {

    private final TenantProperties tenantProperties;

    /**
     * MyBatis-Plus 拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(@org.springframework.beans.factory.annotation.Autowired(required = false) TenantLineInnerInterceptor tenantLineInnerInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件 - 必须放在分页插件之前
        if (tenantLineInnerInterceptor != null) {
            interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        }

        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }

    /**
     * 多租户拦截器
     */
    @Bean
    @ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
    public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
        return new TenantLineInnerInterceptor(new PlusTenantLineHandler(tenantProperties));
    }

}