package org.example.service;

import org.example.domain.model.PasswordLoginBody;
import org.example.domain.model.RegisterBody;
import org.example.domain.vo.LoginVo;

/**
 * 认证服务接口
 *
 * @author Lion Li
 */
public interface IAuthService {

    /**
     * 用户登录
     *
     * @param loginBody 登录信息
     * @return 登录结果
     */
    LoginVo login(PasswordLoginBody loginBody);

    /**
     * 用户注册
     *
     * @param registerBody 注册信息
     * @return 注册结果
     */
    boolean register(RegisterBody registerBody);

    /**
     * 验证码校验
     *
     * @param code 验证码
     * @param uuid 唯一标识
     */
    void validateCaptcha(String code, String uuid);

    /**
     * 用户登出
     */
    void logout();

    // validateUser方法已移除，请使用PasswordAuthStrategy进行用户验证

}