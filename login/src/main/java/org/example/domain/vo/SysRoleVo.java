package org.example.domain.vo;

// import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
// import cn.idev.excel.annotation.ExcelProperty;
// import io.github.linpeilie.annotations.AutoMapper;
// import lombok.Data;
// import org.dromara.common.core.constant.SystemConstants;
// import org.dromara.common.excel.annotation.ExcelDictFormat;
// import org.dromara.common.excel.convert.ExcelDictConvert;
import org.example.domain.entity.SysRole;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色信息视图对象 sys_role
 *
 * @author Michelle.Chung
 */
// @Data
// @ExcelIgnoreUnannotated
// @AutoMapper(target = SysRole.class)
public class SysRoleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    // @ExcelProperty(value = "角色序号")
    private Long roleId;

    /**
     * 角色名称
     */
    // @ExcelProperty(value = "角色名称")
    private String roleName;

    /**
     * 角色权限字符串
     */
    // @ExcelProperty(value = "角色权限")
    private String roleKey;

    /**
     * 显示顺序
     */
    // @ExcelProperty(value = "角色排序")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）
     */
    // @ExcelProperty(value = "数据范围", converter = ExcelDictConvert.class)
    // @ExcelDictFormat(readConverterExp = "1=全部数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限,6=部门及以下或本人数据权限")
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    // @ExcelProperty(value = "菜单树选择项是否关联显示")
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    // @ExcelProperty(value = "部门树选择项是否关联显示")
    private Boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    // @ExcelProperty(value = "角色状态", converter = ExcelDictConvert.class)
    // @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 备注
     */
    // @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 创建时间
     */
    // @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    private boolean flag = false;

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

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}