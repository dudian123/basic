package org.example.handler;

import org.example.utils.R;
import org.example.exception.CaptchaException;
import org.example.exception.CaptchaExpireException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Lion Li
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 验证码错误
     */
    @ExceptionHandler(CaptchaException.class)
    public R<Void> handleCaptchaException(CaptchaException e) {
        return R.fail(e.getMessage());
    }

    /**
     * 验证码过期
     */
    @ExceptionHandler(CaptchaExpireException.class)
    public R<Void> handleCaptchaExpireException(CaptchaExpireException e) {
        return R.fail(e.getMessage());
    }

    /**
     * 用户异常
     */
    @ExceptionHandler(org.example.exception.UserException.class)
    public R<Void> handleUserException(org.example.exception.UserException e) {
        return R.fail(e.getMessage());
    }

    /**
     * StackOverflowError异常
     */
    @ExceptionHandler(StackOverflowError.class)
    public R<Void> handleStackOverflowError(StackOverflowError e) {
        System.err.println("=== StackOverflowError 详细堆栈信息 ===");
        e.printStackTrace();
        System.err.println("=== StackOverflowError 堆栈信息结束 ===");
        return R.fail("服务器内部错误: 栈溢出异常");
    }

    /**
     * 其它异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        System.out.println("=== GlobalExceptionHandler 捕获异常 ===");
        System.out.println("异常类型: " + e.getClass().getName());
        System.out.println("异常消息: " + e.getMessage());
        e.printStackTrace();
        System.out.println("=== 异常处理结束 ===");
        return R.fail("服务器内部错误: " + e.getMessage());
    }
}