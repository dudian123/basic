package org.example.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 三方登录对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SocialLoginBody extends LoginBody {

    /**
     * 第三方登录平台
     */
    @NotBlank(message = "第三方登录平台不能为空")
    private String source;

    /**
     * 第三方登录code
     */
    @NotBlank(message = "第三方登录code不能为空")
    private String socialCode;

    /**
     * 第三方登录socialState
     */
    @NotBlank(message = "第三方登录socialState不能为空")
    private String socialState;

}