package org.example.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Security安全配置
 *
 * @author Lion Li
 */
@Slf4j
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    /**
     * 注册Sa-Token拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，并指定认证规则
        registry.addInterceptor(new SaInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/auth/login",
                "/auth/logout",
                "/captchaImage",
                "/register",
                "/auth/login",
                "/auth/logout",
                "/auth/captcha",
                "/auth/register",
                "/auth/tenant/list",
                "/auth/code",
                "/test/**");
    }

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8091","http://192.168.1.2:8091")
            .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true)
            .maxAge(3600)
            .allowedHeaders("*");
    }
}