package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.SysUser;
import org.example.domain.model.PasswordLoginBody;
import org.example.domain.model.RegisterBody;
import org.example.domain.vo.LoginVo;
import org.example.domain.em.UserType;
import org.example.service.IAuthService;
import org.example.service.ISysUserService;
import org.example.utils.LoginHelper;
import org.springframework.stereotype.Service;

import org.example.config.properties.CaptchaProperties;
import org.example.constant.Constants;
import org.example.exception.CaptchaException;
import org.example.exception.CaptchaExpireException;
import org.example.utils.RedisUtils;
import org.example.utils.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 认证服务实现类
 *
 * @author Lion Li
 */
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private CaptchaProperties captchaProperties;
    
    @Autowired
    private ISysUserService userService;
    


    @Override
    public LoginVo login(PasswordLoginBody loginBody) {
        // 此方法已废弃，登录逻辑已迁移到PasswordAuthStrategy
        throw new UnsupportedOperationException("此登录方法已废弃，请使用PasswordAuthStrategy进行登录");
    }

    @Override
    public boolean register(RegisterBody registerBody) {
        System.out.println("用户注册: " + registerBody.getUsername());
        
        // 验证码校验（如果开启）
        if (captchaProperties.getEnable()) {
            validateCaptcha(registerBody.getCode(), registerBody.getUuid());
        }
        
        // 用户类型校验
        String userType = registerBody.getUserType();
        if (StringUtils.isNotBlank(userType)) {
            if (!UserType.isValidUserType(userType)) {
                throw new RuntimeException("无效的用户类型: " + userType);
            }
        } else {
            // 如果未指定用户类型，默认为系统用户
            userType = UserType.SYS_USER.name();
        }
        
        // 创建SysUser对象
        SysUser user = new SysUser();
        user.setUserName(registerBody.getUsername());
        user.setPassword(registerBody.getPassword());
        user.setNickName(registerBody.getUsername()); // 默认昵称为用户名
        user.setUserType(userType); // 设置用户类型
        
        // 使用数据库注册用户
        boolean result = userService.registerUser(user);
        if (result) {
            System.out.println("用户 " + registerBody.getUsername() + " 注册成功，用户类型: " + userType);
        } else {
            System.out.println("用户 " + registerBody.getUsername() + " 注册失败");
            throw new RuntimeException("注册失败");
        }
        return result;
    }

    @Override
    public void logout() {
        System.out.println("用户退出登录");
        // 使用LoginHelper进行登出
        LoginHelper.logout();
    }



    @Override
    public void validateCaptcha(String code, String uuid) {
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