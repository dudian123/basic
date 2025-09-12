package org.example.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterBody extends LoginBody {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 2, max = 30, message = "用户名长度必须在2-30个字符之间")
    private String username;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 5, max = 30, message = "密码长度必须在5-30个字符之间")
    private String password;

    private String userType;

    // Getter and Setter methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}