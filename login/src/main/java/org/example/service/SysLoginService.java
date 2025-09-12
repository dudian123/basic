package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.constant.Constants;
import org.example.domain.entity.SysUser;
import org.example.domain.model.LoginUser;
import org.example.exception.UserException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 登录校验方法
 *
 * @author Lion Li
 */
@Service
public class SysLoginService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    // 内存存储，当Redis不可用时使用
    private final Map<String, Integer> loginFailStore = new ConcurrentHashMap<>();
    private final Map<String, Long> loginFailTimeStore = new ConcurrentHashMap<>();

    @Value("${user.password.maxRetryCount:5}")
    private int maxRetryCount;

    @Value("${user.password.lockTime:10}")
    private int lockTime;

    // 模拟用户数据存储
    private final Map<String, SysUser> userStore = new ConcurrentHashMap<>();

    public SysLoginService() {
        // 初始化默认用户
        SysUser admin = new SysUser();
        admin.setUserId(1L);
        admin.setUserName("admin");
        admin.setNickName("管理员");
        admin.setPassword("$2a$10$7JB720yubVSOfvVMe6/b/.b4OXhF.ktMfqH9z.jx.jjgs/9cDgCa2"); // 123456的BCrypt加密
        admin.setStatus("0");
        admin.setUserType("sys_user");
        admin.setTenantId("000000");
        admin.setDeptId(100L);
        admin.setDeptName("总公司");
        admin.setEmail("admin@example.com");
        admin.setPhonenumber("15888888888");
        admin.setCreateTime(LocalDateTime.now());
        userStore.put("admin", admin);

        SysUser user = new SysUser();
        user.setUserId(2L);
        user.setUserName("user");
        user.setNickName("普通用户");
        user.setPassword("$2a$10$7JB720yubVSOfvVMe6/b/.b4OXhF.ktMfqH9z.jx.jjgs/9cDgCa2"); // 123456的BCrypt加密
        user.setStatus("0");
        user.setUserType("sys_user");
        user.setTenantId("000000");
        user.setDeptId(101L);
        user.setDeptName("研发部门");
        user.setEmail("user@example.com");
        user.setPhonenumber("15666666666");
        user.setCreateTime(LocalDateTime.now());
        userStore.put("user", user);
    }

    /**
     * 登录校验
     */
    public void checkLogin(String tenantId, String username, boolean passwordError) {
        String errorKey = Constants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;

        // 获取用户登录错误次数，默认为0
        Integer errorNumber = getFailCount(errorKey);
        if (errorNumber == null) {
            errorNumber = 0;
        }

        // 锁定时间内登录 则踢出
        if (errorNumber >= maxRetryCount) {
            recordLoginInfo(tenantId, username, loginFail, 
                String.format("密码输入错误%s次，帐户锁定%s分钟", maxRetryCount, lockTime));
            throw new UserException("user.password.retry.limit.exceed", maxRetryCount, lockTime);
        }

        if (passwordError) {
            // 错误次数递增
            errorNumber++;
            setFailCount(errorKey, errorNumber);
            // 达到规定错误次数 则锁定登录
            if (errorNumber >= maxRetryCount) {
                recordLoginInfo(tenantId, username, loginFail, 
                    String.format("密码输入错误%s次，帐户锁定%s分钟", maxRetryCount, lockTime));
                throw new UserException("user.password.retry.limit.exceed", maxRetryCount, lockTime);
            } else {
                // 未达到规定错误次数
                recordLoginInfo(tenantId, username, loginFail, 
                    String.format("密码输入错误%s次", errorNumber));
                throw new UserException("user.password.retry.limit.count", errorNumber);
            }
        } else {
            // 登录成功 清空错误次数
            clearFailCount(errorKey);
        }
    }

    private Integer getFailCount(String key) {
        try {
            return (Integer) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            // Redis不可用时使用内存存储
            Long expireTime = loginFailTimeStore.get(key);
            if (expireTime != null && System.currentTimeMillis() > expireTime) {
                loginFailStore.remove(key);
                loginFailTimeStore.remove(key);
                return null;
            }
            return loginFailStore.get(key);
        }
    }

    private void setFailCount(String key, int count) {
        try {
            redisTemplate.opsForValue().set(key, count, Duration.ofMinutes(lockTime));
        } catch (Exception e) {
            // Redis不可用时使用内存存储
            loginFailStore.put(key, count);
            loginFailTimeStore.put(key, System.currentTimeMillis() + lockTime * 60 * 1000);
        }
    }

    private void clearFailCount(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            // Redis不可用时使用内存存储
            loginFailStore.remove(key);
            loginFailTimeStore.remove(key);
        }
    }

    /**
     * 校验租户
     *
     * @param tenantId 租户ID
     */
    public void checkTenant(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new UserException("租户ID不能为空");
        }
        // 简化实现，只允许默认租户
        if (!"000000".equals(tenantId)) {
            throw new UserException("租户不存在或已被禁用");
        }
    }

    /**
     * 根据用户名加载用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    public SysUser loadUserByUsername(String username) {
        SysUser user = userStore.get(username);
        if (user == null) {
            System.out.println("登录用户：" + username + " 不存在.");
            throw new UserException("user.not.exists", username);
        } else if ("1".equals(user.getDelFlag())) {
            System.out.println("登录用户：" + username + " 已被删除.");
            throw new UserException("user.password.delete", username);
        } else if ("1".equals(user.getStatus())) {
            System.out.println("登录用户：" + username + " 已被停用.");
            throw new UserException("user.blocked", username);
        }
        return user;
    }

    /**
     * 构建登录用户
     */
    public LoginUser buildLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setTenantId(user.getTenantId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setDeptName(user.getDeptName());
        
        // 设置权限（简化实现）
        Set<String> menuPermission = Set.of("*:*:*"); // 超级管理员权限
        Set<String> rolePermission = Set.of("admin");
        if (!isSuperAdmin(user.getUserId())) {
            menuPermission = Set.of("system:user:list", "system:user:query");
            rolePermission = Set.of("common");
        }
        loginUser.setMenuPermission(menuPermission);
        loginUser.setRolePermission(rolePermission);
        
        return loginUser;
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    public void recordLoginInfo(String tenantId, String username, String status, String message) {
        System.out.println("租户:" + tenantId + ", 用户:" + username + ", 状态:" + status + ", 消息:" + message);
        // 这里可以记录到数据库或日志文件
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    private boolean isSuperAdmin(Long userId) {
        return Long.valueOf(1L).equals(userId);
    }

}