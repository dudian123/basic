package org.example.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.config.properties.CaptchaProperties;
import org.example.constant.Constants;
import org.example.domain.entity.LoginUser;
import org.example.domain.entity.SysUser;
import org.example.domain.model.PasswordLoginBody;
import org.example.domain.vo.LoginVo;
import org.example.exception.CaptchaException;
import org.example.exception.CaptchaExpireException;
import org.example.service.IAuthStrategy;
import org.example.service.ISysUserService;
import org.example.service.ISysRoleService;
import org.example.service.ISysMenuService;
import org.example.utils.LoginHelper;
import org.example.utils.RedisUtils;
import org.example.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 密码认证策略实现
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class PasswordAuthStrategy implements IAuthStrategy {

    @Autowired
    private CaptchaProperties captchaProperties;

    @Autowired
    private ISysUserService sysUserService;
    
    @Autowired
    private ISysRoleService sysRoleService;
    
    @Autowired
    private ISysMenuService sysMenuService;

    @Override
    public LoginVo login(PasswordLoginBody loginBody) {
        log.info("开始登录流程，用户名: {}", loginBody.getUsername());
        
        // 验证码校验（如果开启）- 兼容null，默认关闭
        if (Boolean.TRUE.equals(captchaProperties.getEnable())) {
            log.info("验证码功能已开启，开始校验验证码");
            validateCaptcha(loginBody.getCode(), loginBody.getUuid());
        } else {
            log.info("验证码功能已关闭或未配置，跳过验证码校验");
        }

        try {
            log.info("开始验证用户名密码");
            // 使用数据库验证用户
            SysUser sysUser = sysUserService.validateUserPasswordAndGetUser(loginBody.getUsername(), loginBody.getPassword());
            if (sysUser != null) {
                log.info("用户验证成功，用户ID: {}, 用户类型: {}", sysUser.getUserId(), sysUser.getUserType());
                
                // 构建登录用户信息
                LoginUser loginUser = buildLoginUser(sysUser);
                log.info("构建登录用户信息完成，LoginId: {}", loginUser.getLoginId());
                
                // 使用Sa-Token进行登录
                log.info("开始调用Sa-Token登录");
                LoginHelper.login(loginUser);
                log.info("Sa-Token登录完成");
                
                // 获取token值
                log.info("开始获取token值");
                String tokenValue = StpUtil.getTokenValue();
                log.info("获取到token值: {}", tokenValue);
                
                // 获取用户权限和角色
                log.info("开始获取用户权限和角色");
                List<String> permissions = new ArrayList<>(sysMenuService.selectMenuPermsByUserId(sysUser.getUserId()));
                List<String> roles = new ArrayList<>(sysRoleService.selectRolePermissionByUserId(sysUser.getUserId()));
                log.info("获取到权限数量: {}, 角色数量: {}", permissions.size(), roles.size());
                
                // 构建返回结果
                LoginVo loginVo = new LoginVo();
                loginVo.setAccessToken(tokenValue);
                loginVo.setExpireIn(StpUtil.getTokenTimeout());
                loginVo.setClientId(loginUser.getClientKey());
                loginVo.setUser(sysUser);
                loginVo.setPermissions(permissions);
                loginVo.setRoles(roles);
                
                log.info("用户 {} 使用Sa-Token登录成功", loginBody.getUsername());
                return loginVo;
            } else {
                log.error("用户验证失败，用户名或密码错误");
                throw new org.example.exception.UserException("用户名或密码错误");
            }
        } catch (Exception e) {
            log.error("用户 {} 登录失败：{}", loginBody.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 构建登录用户信息
     */
    private LoginUser buildLoginUser(SysUser sysUser) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setTenantId(sysUser.getTenantId());
        loginUser.setDeptId(sysUser.getDeptId());
        loginUser.setUsername(sysUser.getUserName());
        loginUser.setNickname(sysUser.getNickName());
        // 确保使用数据库中的userType值，如果为空则设置默认值
        String userType = sysUser.getUserType();
        if (userType == null || userType.trim().isEmpty()) {
            userType = "sys_user"; // 设置默认用户类型
        }
        loginUser.setUserType(userType);
        loginUser.setClientKey("pc"); // 默认PC端
        loginUser.setDeviceType("web");
        loginUser.setLoginTime(LocalDateTime.now());
        loginUser.setExpireTime(LocalDateTime.now().plusDays(7));
        
        // 注意：LoginUser类暂时没有权限字段，权限验证通过其他方式实现
        
        log.info("构建LoginUser完成 - userId: {}, userType: {}, loginId: {}", 
                loginUser.getUserId(), loginUser.getUserType(), loginUser.getLoginId());
        return loginUser;
    }

    @Override
    public String getStrategyType() {
        return "password";
    }



    /**
     * 验证码校验
     *
     * @param code 验证码
     * @param uuid 唯一标识
     */
    private void validateCaptcha(String code, String uuid) {
        // 先检查验证码和uuid是否为空
        if (StringUtils.isBlank(code)) {
            throw new CaptchaException("验证码不能为空");
        }
        if (StringUtils.isBlank(uuid)) {
            throw new CaptchaException("验证码标识不能为空");
        }
        
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        RBucket<String> captchaBucket = RedisUtils.getCacheObject(verifyKey);
        String captcha = captchaBucket.get();
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CaptchaExpireException("验证码已失效");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException("验证码错误");
        }
    }
}