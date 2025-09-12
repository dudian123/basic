package org.example.service;

import org.example.domain.model.PasswordLoginBody;
import org.example.domain.vo.LoginVo;

/**
 * 认证策略接口
 *
 * @author Lion Li
 */
public interface IAuthStrategy {

    /**
     * 登录
     *
     * @param loginBody 登录信息
     * @return 登录结果
     */
    LoginVo login(PasswordLoginBody loginBody);

    /**
     * 获取策略类型
     *
     * @return 策略类型
     */
    String getStrategyType();
}