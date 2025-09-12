package org.example.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.properties.TenantProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 租户助手
 *
 * @author ruoyi
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class TenantHelper {

    private static TenantProperties tenantProperties;

    @Autowired
    public void setTenantProperties(TenantProperties tenantProperties) {
        TenantHelper.tenantProperties = tenantProperties;
    }

    private static final String DYNAMIC_TENANT_KEY = "DYNAMIC_TENANT_KEY";
    private static final ThreadLocal<String> TEMP_DYNAMIC_TENANT = new ThreadLocal<>();

    /**
     * 租户功能是否启用
     */
    public static boolean isEnable() {
        return tenantProperties != null && Boolean.TRUE.equals(tenantProperties.getEnable());
    }

    /**
     * 开启忽略租户(开启后需手动调用 {@link #disableIgnore()} 关闭)
     */
    public static void enableIgnore() {
        if (!isEnable()) {
            return;
        }
        InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
    }

    /**
     * 关闭忽略租户
     */
    public static void disableIgnore() {
        if (!isEnable()) {
            return;
        }
        InterceptorIgnoreHelper.clearIgnoreStrategy();
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static void ignore(Runnable handle) {
        enableIgnore();
        try {
            handle.run();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static <T> T ignore(Supplier<T> handle) {
        enableIgnore();
        try {
            return handle.get();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 设置动态租户(一直有效 需要手动清理)
     * <p>
     * 如果为登录状态 则会存储到 redis
     * 如果为未登录状态 则会存储到 threadLocal 中
     *
     * @param tenantId 租户id
     */
    public static void setDynamic(String tenantId) {
        if (!isEnable()) {
            return;
        }
        try {
            // 避免在Sa-Token操作过程中调用StpUtil.isLogin()造成循环调用
            // 优先设置到ThreadLocal，避免触发Sa-Token操作
            TEMP_DYNAMIC_TENANT.set(tenantId);
            
            // 只有在确定不会造成循环调用时才尝试设置到Session
            if (StpUtil.isLogin()) {
                StpUtil.getTokenSession().set(DYNAMIC_TENANT_KEY, tenantId);
            }
        } catch (Exception e) {
            log.debug("设置动态租户ID时发生异常，已设置到ThreadLocal: {}", e.getMessage());
            TEMP_DYNAMIC_TENANT.set(tenantId);
        }
    }

    /**
     * 获取动态租户
     * <p>
     * 如果为登录状态 则会从 redis 获取
     * 如果为未登录状态 则会从 threadLocal 获取
     */
    public static String getDynamic() {
        if (!isEnable()) {
            return null;
        }
        try {
            // 避免在Sa-Token操作过程中调用StpUtil.isLogin()造成循环调用
            // 优先从ThreadLocal获取，避免触发Sa-Token操作
            String threadLocalTenantId = TEMP_DYNAMIC_TENANT.get();
            if (threadLocalTenantId != null) {
                return threadLocalTenantId;
            }
            
            // 只有在确定不会造成循环调用时才尝试从Session获取
            if (StpUtil.isLogin()) {
                return Convert.toStr(StpUtil.getTokenSession().get(DYNAMIC_TENANT_KEY));
            }
        } catch (Exception e) {
            log.debug("获取动态租户ID时发生异常，返回ThreadLocal值: {}", e.getMessage());
        }
        return TEMP_DYNAMIC_TENANT.get();
    }

    /**
     * 清除动态租户
     */
    public static void clearDynamic() {
        if (!isEnable()) {
            return;
        }
        try {
            // 避免在Sa-Token操作过程中调用StpUtil.isLogin()造成循环调用
            // 优先清除ThreadLocal
            TEMP_DYNAMIC_TENANT.remove();
            
            // 只有在确定不会造成循环调用时才尝试清除Session
            if (StpUtil.isLogin()) {
                StpUtil.getTokenSession().delete(DYNAMIC_TENANT_KEY);
            }
        } catch (Exception e) {
            log.debug("清除动态租户ID时发生异常，已清除ThreadLocal: {}", e.getMessage());
            TEMP_DYNAMIC_TENANT.remove();
        }
    }

    /**
     * 在动态租户中执行
     *
     * @param tenantId 租户ID
     * @param handle 处理执行方法
     */
    public static void dynamic(String tenantId, Runnable handle) {
        setDynamic(tenantId);
        try {
            handle.run();
        } finally {
            clearDynamic();
        }
    }

    /**
     * 在动态租户中执行
     *
     * @param tenantId 租户ID
     * @param handle 处理执行方法
     */
    public static <T> T dynamic(String tenantId, Supplier<T> handle) {
        setDynamic(tenantId);
        try {
            return handle.get();
        } finally {
            clearDynamic();
        }
    }

    /**
     * 获取当前租户id(动态租户优先)
     */
    public static String getTenantId() {
        if (!isEnable()) {
            return null;
        }
        String tenantId = getDynamic();
        if (tenantId != null) {
            return tenantId;
        }
        // 避免循环调用LoginHelper.getTenantId()，直接返回默认租户ID
        // 在Sa-Token操作过程中，不应该尝试获取登录用户信息
        log.debug("未找到动态租户ID，返回默认租户ID: 000000");
        return "000000";
    }

}

/**
 * 忽略策略
 */
class IgnoreStrategy {
    private boolean tenantLine;

    public static IgnoreStrategyBuilder builder() {
        return new IgnoreStrategyBuilder();
    }

    public boolean isTenantLine() {
        return tenantLine;
    }

    public void setTenantLine(boolean tenantLine) {
        this.tenantLine = tenantLine;
    }

    public static class IgnoreStrategyBuilder {
        private boolean tenantLine;

        public IgnoreStrategyBuilder tenantLine(boolean tenantLine) {
            this.tenantLine = tenantLine;
            return this;
        }

        public IgnoreStrategy build() {
            IgnoreStrategy strategy = new IgnoreStrategy();
            strategy.setTenantLine(this.tenantLine);
            return strategy;
        }
    }
}

/**
 * 拦截器忽略助手
 */
class InterceptorIgnoreHelper {
    private static final ThreadLocal<IgnoreStrategy> IGNORE_STRATEGY_THREAD_LOCAL = new ThreadLocal<>();

    public static void handle(IgnoreStrategy ignoreStrategy) {
        IGNORE_STRATEGY_THREAD_LOCAL.set(ignoreStrategy);
    }

    public static IgnoreStrategy getIgnoreStrategy() {
        return IGNORE_STRATEGY_THREAD_LOCAL.get();
    }

    public static void clearIgnoreStrategy() {
        IGNORE_STRATEGY_THREAD_LOCAL.remove();
    }

    public static boolean isTenantIgnore() {
        IgnoreStrategy strategy = getIgnoreStrategy();
        return strategy != null && strategy.isTenantLine();
    }
}