package org.example.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录租户对象
 *
 * @author Lion Li
 */
@Data
public class LoginTenantVo {

    /**
     * 是否开启租户
     */
    private Boolean tenantEnabled;

    /**
     * 租户对象列表
     */
    private List<TenantListVo> voList;

    // Getter and Setter methods
    public Boolean getTenantEnabled() {
        return tenantEnabled;
    }

    public void setTenantEnabled(Boolean tenantEnabled) {
        this.tenantEnabled = tenantEnabled;
    }

    public List<TenantListVo> getVoList() {
        return voList;
    }

    public void setVoList(List<TenantListVo> voList) {
        this.voList = voList;
    }

}