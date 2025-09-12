package org.example.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.HttpStatus;
import org.example.utils.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token异常处理器
 *
 * @author Lion Li
 */
@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException e) {
        log.error("权限校验失败: {}", e.getMessage());
        return R.fail(HttpStatus.FORBIDDEN, "无访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException e) {
        log.error("角色权限校验失败: {}", e.getMessage());
        return R.fail(HttpStatus.FORBIDDEN, "请求访问：" + e.getRole() + "，权限不足，请联系管理员授权");
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException e) {
        log.error("认证失败，无法访问系统资源: {}", e.getMessage());
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供Token";
            case NotLoginException.INVALID_TOKEN -> "Token无效";
            case NotLoginException.TOKEN_TIMEOUT -> "Token已过期";
            case NotLoginException.BE_REPLACED -> "Token已被顶下线";
            case NotLoginException.KICK_OUT -> "Token已被踢下线";
            case NotLoginException.TOKEN_FREEZE -> "Token已被冻结";
            case NotLoginException.NO_PREFIX -> "未按照指定前缀提交Token";
            default -> "当前会话未登录";
        };
        return R.fail(HttpStatus.UNAUTHORIZED, message);
    }
}