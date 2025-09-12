package org.example.domain.em;

/**
 * 用户类型枚举
 *
 * @author Lion Li
 */
public enum UserType {

    /**
     * pc端
     */
    SYS_USER("sys_user", "pc端"),

    /**
     * app端
     */
    APP_USER("app_user", "app端"),

    /**
     * 小程序端
     */
    XCX_USER("xcx_user", "小程序端");

    private final String userType;
    private final String info;

    UserType(String userType, String info) {
        this.userType = userType;
        this.info = info;
    }

    public String getUserType() {
        return userType;
    }

    public String getInfo() {
        return info;
    }

    /**
     * 根据用户类型字符串获取枚举
     *
     * @param userType 用户类型字符串
     * @return 用户类型枚举
     */
    public static UserType getUserType(String userType) {
        if (userType == null || userType.trim().isEmpty()) {
            return SYS_USER; // 默认返回系统用户类型
        }
        
        for (UserType type : UserType.values()) {
            if (type.getUserType().equals(userType)) {
                return type;
            }
        }
        
        // 如果找不到匹配的类型，返回默认类型
        return SYS_USER;
    }

    /**
     * 校验用户类型是否有效
     *
     * @param userType 用户类型字符串
     * @return 是否有效
     */
    public static boolean isValidUserType(String userType) {
        if (userType == null || userType.trim().isEmpty()) {
            return false;
        }
        
        for (UserType type : UserType.values()) {
            if (type.getUserType().equals(userType)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        return this.userType;
    }
}