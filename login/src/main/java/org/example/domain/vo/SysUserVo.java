package org.example.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.domain.entity.SysUser;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息视图对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
public class SysUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（sys_user系统用户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 密码
     */
    @JsonIgnore
    @JsonProperty
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 部门名
     */
    private String deptName;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;

    /**
     * 角色列表
     */
    // private List<SysRoleVo> roles; // 暂时注释，后续实现

    /**
     * 角色组
     */
    private String roleGroup;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 数据权限 当前角色ID
     */
    private Long roleId;

    // 构造方法，从SysUser转换
    public static SysUserVo fromSysUser(SysUser user) {
        if (user == null) {
            return null;
        }
        SysUserVo vo = new SysUserVo();
        vo.setUserId(user.getUserId());
        vo.setTenantId(user.getTenantId());
        vo.setDeptId(user.getDeptId());
        vo.setUserName(user.getUserName());
        vo.setNickName(user.getNickName());
        vo.setUserType(user.getUserType());
        vo.setEmail(user.getEmail());
        vo.setPhonenumber(user.getPhonenumber());
        vo.setSex(user.getSex());
        vo.setAvatar(user.getAvatar());
        vo.setPassword(user.getPassword());
        vo.setStatus(user.getStatus());
        vo.setLoginIp(user.getLoginIp());
        vo.setLoginDate(user.getLoginDate());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}