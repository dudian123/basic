package org.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.ApplicationRunner;

/**
 * DDL配置类 - 显式禁用DDL功能
 * 解决MyBatis-Plus ddlApplicationRunner bean类型错误问题
 *
 * @author example
 */
@Configuration
public class DdlConfig {

    /**
     * 禁用DDL应用程序运行器
     * 返回null bean来避免Spring Boot尝试加载默认的ddlApplicationRunner
     */
    @Bean("ddlApplicationRunner")
    @ConditionalOnProperty(
        prefix = "mybatis-plus.global-config", 
        name = "enable-sql-runner", 
        havingValue = "false", 
        matchIfMissing = true
    )
    public ApplicationRunner ddlApplicationRunner() {
        return args -> {};
    }
}