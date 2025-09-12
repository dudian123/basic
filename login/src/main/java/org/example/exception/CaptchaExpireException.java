package org.example.exception;

/**
 * 验证码失效异常类
 *
 * @author Lion Li
 */
public class CaptchaExpireException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException(String msg) {
        super(msg);
    }
}