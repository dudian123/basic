package org.example.domain.bo;

// import io.github.linpeilie.annotations.AutoMapper;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import org.dromara.common.core.constant.SystemConstants;
// import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.example.domain.entity.SysRole;

/**
 * 角色信息业务对象 sys_role
 *
 * @author Michelle.Chung
 */

// @Data
// @NoArgsConstructor
// @EqualsAndHashCode(callSuper = true)
// @AutoMapper(target = SysRole.class, reverseConvertGenerate = false)
public class SysRoleBo {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    // @NotBlank(message = "角色名称不能为空")
    // @Size(min = 0, max = 30, message = "角色名称长度不能超过{max}个字符")
    private String roleName;

    /**
     * 角色权限字符串
     */
    // @NotBlank(message = "角色权限字符串不能为空")
    // @Size(min = 0, max = 100, message = "权限字符长度不能超过{max}个字符")
    private String roleKey;

    /**
     * 显示顺序
     */
    // @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）
     */
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    private Boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单组
     */
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    private Long[] deptIds;

    public SysRoleBo() {
    }

    public SysRoleBo(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isSuperAdmin() {
        // return SystemConstants.SUPER_ADMIN_ID.equals(this.roleId);
        return Long.valueOf(1L).equals(this.roleId);
    }

    // Getters and Setters
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public Integer getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(Integer roleSort) {
        this.roleSort = roleSort;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public Boolean getMenuCheckStrictly() {
        return menuCheckStrictly;
    }

    public void setMenuCheckStrictly(Boolean menuCheckStrictly) {
        this.menuCheckStrictly = menuCheckStrictly;
    }

    public Boolean getDeptCheckStrictly() {
        return deptCheckStrictly;
    }

    public void setDeptCheckStrictly(Boolean deptCheckStrictly) {
        this.deptCheckStrictly = deptCheckStrictly;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long[] getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(Long[] menuIds) {
        this.menuIds = menuIds;
    }

    public Long[] getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(Long[] deptIds) {
        this.deptIds = deptIds;
    }
}