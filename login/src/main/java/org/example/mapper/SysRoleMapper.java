package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.domain.entity.SysRole;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id " +
            "WHERE r.del_flag = '0' AND ur.user_id = #{userId}")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id " +
            "WHERE r.status = '0' AND r.del_flag = '0' AND ur.user_id = #{userId}")
    List<String> selectRolePermissionByUserId(@Param("userId") Long userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Select("SELECT * FROM sys_role WHERE del_flag = '0' ORDER BY role_sort")
    List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Select("SELECT r.role_id FROM sys_role r " +
            "LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Long> selectRoleListByUserId(@Param("userId") Long userId);

    /**
     * 校验角色名称是否唯一
     *
     * @param roleName 角色名称
     * @param roleId   角色ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_role WHERE role_name = #{roleName} AND role_id != #{roleId} AND del_flag = '0'")
    int checkRoleNameUnique(@Param("roleName") String roleName, @Param("roleId") Long roleId);

    /**
     * 校验角色权限是否唯一
     *
     * @param roleKey 角色权限
     * @param roleId  角色ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_role WHERE role_key = #{roleKey} AND role_id != #{roleId} AND del_flag = '0'")
    int checkRoleKeyUnique(@Param("roleKey") String roleKey, @Param("roleId") Long roleId);

    /**
     * 分页查询角色列表
     *
     * @param queryWrapper 查询条件
     * @return 角色列表
     */
    List<org.example.domain.vo.SysRoleVo> selectPageRoleList(@Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<SysRole> queryWrapper);

    /**
     * 查询角色列表
     *
     * @param queryWrapper 查询条件
     * @return 角色列表
     */
    List<org.example.domain.vo.SysRoleVo> selectRoleList(@Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<SysRole> queryWrapper);

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    org.example.domain.vo.SysRoleVo selectRoleById(@Param("roleId") Long roleId);

    /**
     * 新增角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int insertRole(SysRole role);

    /**
     * 修改角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRole(SysRole role);

    /**
     * 删除角色信息
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleById(@Param("roleId") Long roleId);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    int deleteRoleByIds(Long[] roleIds);
}