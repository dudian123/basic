package org.example.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.RedisUtils;
import org.springframework.stereotype.Component;

/**
 * 用户行为监听器
 * 监听Sa-Token的登录、退出等事件，处理相关的Redis数据清理
 *
 * @author example
 */
// @Component
@Slf4j
public class UserActionListener implements SaTokenListener {

    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        log.info("用户登录成功，用户ID: {}, token: {}", loginId, tokenValue);
        // 这里可以添加登录时的额外处理逻辑，比如记录登录日志、更新在线用户信息等
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        log.info("用户退出登录，用户ID: {}, token: {}", loginId, tokenValue);
        
        // 清理与该token相关的Redis数据
        // 如果有在线用户信息缓存，可以在这里清理
        // 例如：RedisUtils.deleteObject("online_user:" + tokenValue);
        
        // 清理其他可能与用户会话相关的缓存数据
        // 例如：用户权限缓存、临时数据等
        
        log.info("已清理用户 {} 的相关缓存数据", loginId);
    }

    /**
     * 每次被踢下线时触发
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        log.info("用户被踢下线，用户ID: {}, token: {}", loginId, tokenValue);
        // 清理相关缓存数据，逻辑与doLogout类似
    }

    /**
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        log.info("用户被顶下线，用户ID: {}, token: {}", loginId, tokenValue);
        // 清理相关缓存数据，逻辑与doLogout类似
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        log.info("用户被封禁，用户ID: {}, 服务: {}, 级别: {}, 时长: {}", loginId, service, level, disableTime);
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        log.info("用户被解封，用户ID: {}, 服务: {}", loginId, service);
    }

    /**
     * 每次打开二级认证时触发
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
        log.info("开启二级认证，token: {}, 服务: {}, 时长: {}", tokenValue, service, safeTime);
    }

    /**
     * 每次关闭二级认证时触发
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
        log.info("关闭二级认证，token: {}, 服务: {}", tokenValue, service);
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
        log.debug("创建Session，ID: {}", id);
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
        log.debug("注销Session，ID: {}", id);
    }

    /**
     * 每次Token续期时触发
     */
    @Override
    public void doRenewTimeout(String loginType, Object loginId, long timeout) {
        log.debug("Token续期，用户ID: {}, 超时时间: {}", loginId, timeout);
    }
}