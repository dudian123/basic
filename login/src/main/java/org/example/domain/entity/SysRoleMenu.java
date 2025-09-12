package org.example.domain.entity;

// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.annotation.TableId;
// import com.baomidou.mybatisplus.annotation.TableName;
// import lombok.Data;

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author Lion Li
 */

// @Data
// @TableName("sys_role_menu")
public class SysRoleMenu {

    /**
     * 角色ID
     */
    // @TableId(type = IdType.INPUT)
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
}