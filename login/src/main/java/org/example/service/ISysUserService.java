package org.example.service;

import org.example.domain.entity.SysUser;
import org.example.domain.vo.SysUserVo;
import org.example.domain.PageQuery;
import org.example.domain.TableDataInfo;
import java.util.List;

/**
 * 用户 业务层
 *
 * @author Lion Li
 */
public interface ISysUserService {

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(Long userId);

    /**
     * 通过用户ID查询用户VO
     *
     * @param userId 用户ID
     * @return 用户VO对象信息
     */
    org.example.domain.vo.SysUserVo selectUserVoById(Long userId);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名
     * @return 结果
     */
    boolean checkUserNameUnique(String userName);

    /**
     * 验证用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 验证结果
     */
    boolean validateUserPassword(String userName, String password);

    /**
     * 验证用户密码并返回用户信息
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户信息，验证失败返回null
     */
    SysUser validateUserPasswordAndGetUser(String userName, String password);

    /**
     * 注册用户
     *
     * @param user 用户信息
     * @return 注册结果
     */
    boolean registerUser(SysUser user);

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userId 用户ID
     * @return 结果
     */
    String selectUserRoleGroup(Long userId);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @return 用户信息集合信息
     */
    List<SysUserVo> selectAllocatedList(Long roleId, String userName, String phonenumber);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @return 用户信息集合信息
     */
    List<SysUserVo> selectUnallocatedList(Long roleId, String userName, String phonenumber);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @param pageQuery 分页查询参数
     * @return 用户信息分页集合信息
     */
    TableDataInfo<SysUserVo> selectAllocatedList(Long roleId, String userName, String phonenumber, PageQuery pageQuery);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @param pageQuery 分页查询参数
     * @return 用户信息分页集合信息
     */
    TableDataInfo<SysUserVo> selectUnallocatedList(Long roleId, String userName, String phonenumber, PageQuery pageQuery);

    /**
     * 查询用户列表
     *
     * @return 用户集合
     */
    List<SysUserVo> selectUserList();

    /**
     * 分页查询用户列表
     *
     * @param pageQuery 分页查询参数
     * @return 分页结果
     */
    TableDataInfo<SysUserVo> selectUserList(PageQuery pageQuery);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(SysUser user);

    /**
     * 批量逻辑删除用户
     *
     * @param userIds 用户ID数组
     * @return 影响行数
     */
    int deleteUserByIds(Long[] userIds);
}