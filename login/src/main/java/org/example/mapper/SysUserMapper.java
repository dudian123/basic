package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.entity.SysUser;

import java.util.List;

/**
 * 系统用户数据访问层
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(@Param("userName") String userName);
    
    /**
     * 根据用户名统计用户数量
     */
    int countByUserName(@Param("userName") String userName);
    
    /**
     * 根据用户ID查询用户
     */
    SysUser selectUserById(@Param("userId") Long userId);
    
    /**
     * 插入新用户
     */
    int insertUser(SysUser user);

    /**
     * 更新用户信息
     */
    int updateUser(SysUser user);
    
    /**
     * 批量逻辑删除用户（设置 del_flag='2'）
     */
    int deleteUserByIds(@Param("userIds") Long[] userIds);
    
    /**
     * 查询用户列表
     */
    List<SysUser> selectUserList();
    
    /**
     * 查询已分配指定角色的用户列表
     */
    List<SysUser> selectAllocatedUserList(@Param("roleId") Long roleId, @Param("userName") String userName, @Param("phonenumber") String phonenumber);
    
    /**
     * 查询未分配指定角色的用户列表
     */
    List<SysUser> selectUnallocatedUserList(@Param("roleId") Long roleId, @Param("userName") String userName, @Param("phonenumber") String phonenumber);
    
    /**
     * 分页查询已分配指定角色的用户列表
     */
    IPage<SysUser> selectAllocatedUserPage(Page<SysUser> page, @Param("roleId") Long roleId, @Param("userName") String userName, @Param("phonenumber") String phonenumber);
    
    /**
     * 分页查询未分配指定角色的用户列表
     */
    IPage<SysUser> selectUnallocatedUserPage(Page<SysUser> page, @Param("roleId") Long roleId, @Param("userName") String userName, @Param("phonenumber") String phonenumber);
}