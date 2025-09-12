package org.example.exception;

/**
 * 用户信息异常类
 *
 * @author Lion Li
 */
public class UserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserException(String message, Object... args) {
        super(String.format(message, args));
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

}