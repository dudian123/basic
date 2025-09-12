package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.properties.CaptchaProperties;
import org.example.constant.Constants;
import org.example.domain.em.CaptchaType;
import org.example.domain.em.LimitType;
import org.example.domain.entity.CaptchaVo;
import org.example.domain.model.LoginBody;
import org.example.domain.model.PasswordLoginBody;
import org.example.domain.model.RegisterBody;
import org.example.domain.vo.LoginTenantVo;
import org.example.domain.vo.LoginVo;
import org.example.domain.vo.TenantListVo;
import org.example.domain.vo.UserInfoVo;
import org.example.domain.vo.SysUserVo;
import org.example.domain.vo.SysMenuVo;
import org.example.service.ISysUserService;
import org.example.service.ISysMenuService;
import org.example.utils.LoginHelper;
import org.example.rateLimit.RateLimiter;
import org.example.service.IAuthService;
import org.example.service.ISysConfigService;
import org.example.service.impl.PasswordAuthStrategy;
import org.example.utils.R;
import org.example.utils.RedisUtils;
import org.example.utils.ReflectUtils;
import org.example.utils.SpringUtils;
import org.example.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 认证授权 控制器
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;
    private final PasswordAuthStrategy passwordAuthStrategy;
    private final ISysConfigService configService;
    private final ISysUserService userService;
    private final ISysMenuService menuService;
    
    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public R<LoginVo> login(@RequestBody PasswordLoginBody loginBody) {
        try {
            // 生成令牌
            LoginVo loginVo = passwordAuthStrategy.login(loginBody);
            return R.ok(loginVo);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @SaCheckLogin
    @GetMapping("/getInfo")
    public R<UserInfoVo> getInfo() {
        try {
            // 获取当前登录用户ID
            Long userId = LoginHelper.getUserId();
            if (userId == null) {
                return R.fail("用户未登录");
            }

            // 创建用户信息响应对象
            UserInfoVo userInfoVo = new UserInfoVo();

            // 获取用户基本信息
            SysUserVo userVo = userService.selectUserVoById(userId);
            if (userVo == null) {
                return R.fail("用户信息不存在");
            }
            userInfoVo.setUser(userVo);

            // 获取用户菜单列表
            List<SysMenuVo> menus = menuService.selectMenuList(userId);
            userInfoVo.setMenus(menus);

            // 设置角色和权限（暂时设置为空列表，后续实现）
            userInfoVo.setRoles(new ArrayList<>());
            userInfoVo.setPermissions(new ArrayList<>());

            log.info("获取用户信息成功，用户ID: {}", userId);
            return R.ok(userInfoVo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            return R.fail("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 退出登录
     */
    @SaCheckLogin
    @PostMapping("/logout")
    public R<Void> logout() {
        try {
            // 获取当前用户的loginId
            Object loginId = StpUtil.getLoginId();
            String tokenValue = StpUtil.getTokenValue();
            
            // Sa-Token默认退出
            StpUtil.logout();
            
            // 手动清理Redis中的相关数据
            clearRedisTokenData(loginId, tokenValue);
            
            return R.ok("退出成功");
        } catch (Exception e) {
            log.error("退出登录失败", e);
            return R.fail("退出登录失败: " + e.getMessage());
        }
    }

    /**
     * 清理Redis中的Sa-Token相关数据
     */
    private void clearRedisTokenData(Object loginId, String tokenValue) {
        try {
            // 清理token相关的key
            if (StringUtils.isNotBlank(tokenValue)) {
                String tokenKey = "Authorization:login:token:" + tokenValue;
                RedisUtils.deleteObject(tokenKey);
                log.info("清理token key: {}", tokenKey);
            }
            
            // 清理用户相关的key（根据实际的key格式调整）
            if (loginId != null) {
                String userKey = "Authorization:login:last-activity:" + loginId;
                RedisUtils.deleteObject(userKey);
                log.info("清理用户活动key: {}", userKey);
                
                // 清理session相关的key
                String sessionKey = "Authorization:session:" + loginId;
                RedisUtils.deleteObject(sessionKey);
                log.info("清理session key: {}", sessionKey);
            }
            
            // 批量清理可能存在的其他相关key
            Collection<String> allKeys = RedisUtils.keys("Authorization:*");
            for (String key : allKeys) {
                if ((loginId != null && key.contains(String.valueOf(loginId))) || 
                    (StringUtils.isNotBlank(tokenValue) && key.contains(tokenValue))) {
                    RedisUtils.deleteObject(key);
                    log.info("清理相关key: {}", key);
                }
            }
        } catch (Exception e) {
            log.error("清理Redis数据失败", e);
        }
    }

    /**
     * 用户注册
     */
    @SaCheckPermission("login:auth:register")
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBody user) {
        try {
            if (!configService.selectRegisterEnabled()) {
                return R.fail("当前系统没有开启注册功能！");
            }
            authService.register(user);
            return R.ok();
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage(), e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public R<CaptchaVo> getCode() {
        return R.ok(getCodeImpl());
    }

    /**
     * 生成验证码实现
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    public CaptchaVo getCodeImpl() {
        // 验证码功能已禁用，返回空的验证码对象
        CaptchaVo captchaVo = new CaptchaVo();
        Boolean captchaEnabled = captchaProperties.getEnable();
        log.info("验证码配置状态: {}", captchaEnabled);
        captchaVo.setCaptchaEnabled(captchaEnabled != null ? captchaEnabled : false); // 从配置文件读取验证码启用状态，默认false
        captchaVo.setUuid(IdUtil.simpleUUID());
        captchaVo.setImg("");
        return captchaVo;
    }

    /**
     * 租户列表
     *
     * @param request 请求对象
     * @return 租户列表
     */
    @GetMapping("/tenant/list")
    public R<LoginTenantVo> tenantList(HttpServletRequest request) {
        List<TenantListVo> tenantList = new ArrayList<>();
        LoginTenantVo vo = new LoginTenantVo();
        vo.setVoList(tenantList);
        vo.setTenantEnabled(false);
        return R.ok(vo);
    }
}