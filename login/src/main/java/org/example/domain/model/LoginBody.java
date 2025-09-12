package org.example.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录对象
 *
 * @author Lion Li
 */
@Data
public class LoginBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 客户端id
     */
    @NotBlank(message = "客户端ID不能为空")
    private String clientId;

    /**
     * 授权类型
     */
    @NotBlank(message = "授权类型不能为空")
    private String grantType;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;

    // Getter and Setter methods
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}