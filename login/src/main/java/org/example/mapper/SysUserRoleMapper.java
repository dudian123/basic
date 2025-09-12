package org.example.mapper;

// import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.example.domain.entity.SysUserRole;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author Lion Li
 */
// public interface SysUserRoleMapper extends BaseMapperPlus<SysUserRole, SysUserRole> {
public interface SysUserRoleMapper {

    /**
     * 根据角色ID查询关联的用户ID列表
     *
     * @param roleId 角色ID
     * @return 关联到指定角色的用户ID列表
     */
    // List<Long> selectUserIdsByRoleId(Long roleId);

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    int deleteByUserId(Long userId);

    /**
     * 根据角色ID与用户ID集合删除关联
     *
     * @param roleId  角色ID
     * @param userIds 用户ID集合
     * @return 删除的记录数
     */
    int deleteByRoleIdAndUserIds(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);

    /**
     * 批量插入用户角色关联
     *
     * @param userRoles 用户角色关联列表
     * @return 插入的记录数
     */
    int insertBatch(List<SysUserRole> userRoles);

    /**
     * 插入单个用户角色关联
     *
     * @param userRole 用户角色关联
     * @return 插入的记录数
     */
    int insert(SysUserRole userRole);

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);

}