package org.example.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.LoginUser;

/**
 * 登录鉴权助手
 * <p>
 * user_type 为 用户类型 同一个用户表 可以有多种用户类型 例如 pc,app
 * deivce 为 设备类型 同一个用户类型 可以有 多种设备类型 例如 web,ios
 * 可以组成 用户类型与设备类型多对多的 权限灵活控制
 * <p>
 * 多用户体系 针对 多种用户类型 但权限控制不一致
 * 可以组成 多用户类型表与多设备类型 分别控制权限
 *
 * @author example
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String TENANT_KEY = "tenantId";
    public static final String USER_KEY = "userId";
    public static final String USER_NAME_KEY = "username";
    public static final String DEPT_KEY = "deptId";
    public static final String CLIENT_KEY = "clientId";

    /**
     * 登录系统
     *
     * @param loginUser 登录用户信息
     */
    public static void login(LoginUser loginUser) {
        // 使用Sa-Token进行登录，并设置额外信息
        StpUtil.login(loginUser.getLoginId(), 
            new cn.dev33.satoken.stp.SaLoginModel()
                .setExtra(TENANT_KEY, loginUser.getTenantId())
                .setExtra(USER_KEY, loginUser.getUserId())
                .setExtra(USER_NAME_KEY, loginUser.getUsername())
                .setExtra(DEPT_KEY, loginUser.getDeptId())
                .setExtra(CLIENT_KEY, loginUser.getClientKey())
        );
        // 将登录用户信息存储到Session中
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
        
        // 同时存储到当前请求的Storage中，供getLoginUser()使用
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 退出登录
     */
    public static void logout() {
        StpUtil.logout();
    }

    /**
     * 获取用户(多级缓存)
     */
    @SuppressWarnings("unchecked")
    public static LoginUser getLoginUser() {
        try {
            // 只从当前请求的Storage中获取，避免调用任何Sa-Token方法造成循环调用
            LoginUser loginUser = (LoginUser) SaHolder.getStorage().get(LOGIN_USER_KEY);
            if (loginUser != null) {
                return loginUser;
            }
            
            // 如果Storage中没有，直接返回null，不再尝试从Session获取
            // 这样可以完全避免循环调用问题
            log.debug("当前请求Storage中未找到登录用户信息");
            return null;
        } catch (Exception e) {
            log.debug("获取登录用户时发生异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户基于token
     */
    @SuppressWarnings("unchecked")
    public static LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        if (ObjectUtil.isNull(loginUser)) {
            String loginId = StpUtil.getLoginIdAsString();
            String[] strs = loginId.split(":");
            // 用户id在loginId的最后一部分
            return Long.valueOf(strs[strs.length - 1]);
        }
        return loginUser.getUserId();
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        try {
            // 避免在Sa-Token操作过程中调用StpUtil.getExtra()造成循环调用
            // 优先从LoginUser对象获取租户ID
            LoginUser loginUser = getLoginUser();
            if (loginUser != null && loginUser.getTenantId() != null) {
                return loginUser.getTenantId();
            }
            
            // 如果LoginUser中没有租户ID，则尝试从Extra获取
            return (String) StpUtil.getExtra(TENANT_KEY);
        } catch (Exception e) {
            log.debug("获取租户ID时发生异常，返回默认值: {}", e.getMessage());
            return "000000"; // 返回默认租户ID
        }
    }

    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return (String) StpUtil.getExtra(USER_NAME_KEY);
    }

    /**
     * 获取用户类型
     */
    public static String getUserType() {
        String loginId = StpUtil.getLoginIdAsString();
        return loginId.split(":")[0];
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 是否为管理员
     *
     * @return 结果
     */
    public static boolean isAdmin() {
        return isAdmin(getUserId());
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isSuperAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 是否为超级管理员
     *
     * @return 结果
     */
    public static boolean isSuperAdmin() {
        return isSuperAdmin(getUserId());
    }

}