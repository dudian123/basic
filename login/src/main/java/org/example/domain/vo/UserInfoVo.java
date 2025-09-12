package org.example.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 用户信息响应对象
 *
 * @author example
 */
@Data
public class UserInfoVo {

    /**
     * 用户信息
     */
    private SysUserVo user;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 菜单列表
     */
    private List<SysMenuVo> menus;

    // Getter and Setter methods
    public SysUserVo getUser() {
        return user;
    }

    public void setUser(SysUserVo user) {
        this.user = user;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<SysMenuVo> getMenus() {
        return menus;
    }

    public void setMenus(List<SysMenuVo> menus) {
        this.menus = menus;
    }
}