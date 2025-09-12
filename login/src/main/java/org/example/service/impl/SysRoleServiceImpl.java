package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.entity.SysRole;
import org.example.domain.entity.SysRoleDept;
import org.example.domain.entity.SysRoleMenu;
import org.example.domain.entity.SysUserRole;
import org.example.domain.bo.SysRoleBo;
import org.example.domain.vo.SysRoleVo;
import org.example.domain.TableDataInfo;
import org.example.domain.PageQuery;
import org.example.mapper.SysRoleDeptMapper;
import org.example.mapper.SysRoleMapper;
import org.example.mapper.SysRoleMenuMapper;
import org.example.mapper.SysUserRoleMapper;
import org.example.service.ISysRoleService;
import java.time.LocalDateTime;
// import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色 业务层处理
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper baseMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysRoleDeptMapper roleDeptMapper;
    private final SysUserRoleMapper userRoleMapper;

    public SysRoleServiceImpl(SysRoleMapper baseMapper, SysRoleMenuMapper roleMenuMapper, SysRoleDeptMapper roleDeptMapper, SysUserRoleMapper userRoleMapper) {
        this.baseMapper = baseMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.roleDeptMapper = roleDeptMapper;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @param pageQuery 分页查询参数
     * @return 角色数据分页信息
     */
    @Override
    public TableDataInfo<SysRoleVo> selectPageRoleList(SysRoleBo role, PageQuery pageQuery) {
        log.info("开始分页查询角色列表，分页参数: {}", pageQuery);

        // 使用 MyBatis-Plus 分页插件进行物理分页
        Page<SysRole> page = pageQuery.build();
        
        // 构建查询条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("r.del_flag", "0");
        if (role != null) {
            if (StringUtils.isNotBlank(role.getRoleName())) {
                wrapper.like("r.role_name", role.getRoleName());
            }
            if (StringUtils.isNotBlank(role.getRoleKey())) {
                wrapper.like("r.role_key", role.getRoleKey());
            }
            if (StringUtils.isNotBlank(role.getStatus())) {
                wrapper.eq("r.status", role.getStatus());
            }
        }
        wrapper.orderByAsc("r.role_sort");
        
        // 使用XML中定义的selectPageRoleList方法
        List<SysRoleVo> roleVoList = baseMapper.selectPageRoleList(wrapper);
        
        log.info("分页查询到记录数: {}", roleVoList != null ? roleVoList.size() : 0);

        // 手动分页处理
        int total = roleVoList != null ? roleVoList.size() : 0;
        int pageNum = pageQuery.getPageNum();
        int pageSize = pageQuery.getPageSize();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<SysRoleVo> pagedList = new ArrayList<>();
        if (roleVoList != null && startIndex < total) {
            pagedList = roleVoList.subList(startIndex, endIndex);
        }
        
        return TableDataInfo.build(pagedList, total);
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<SysRoleVo> selectRoleList(SysRoleBo role) {
        // 构建查询条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("r.del_flag", "0");
        if (role != null) {
            if (StringUtils.isNotBlank(role.getRoleName())) {
                wrapper.like("r.role_name", role.getRoleName());
            }
            if (StringUtils.isNotBlank(role.getRoleKey())) {
                wrapper.like("r.role_key", role.getRoleKey());
            }
            if (StringUtils.isNotBlank(role.getStatus())) {
                wrapper.eq("r.status", role.getStatus());
            }
        }
        wrapper.orderByAsc("r.role_sort");
        
        // 使用XML中定义的selectRoleList方法
        List<SysRoleVo> roleVoList = baseMapper.selectRoleList(wrapper);
        System.out.println("=== selectRoleList 返回结果数量: " + (roleVoList != null ? roleVoList.size() : "null") + " ===");
        if (roleVoList != null && !roleVoList.isEmpty()) {
            System.out.println("=== 第一个角色: " + roleVoList.get(0).getRoleName() + " ===");
        }
        return roleVoList;
    }
    
    private List<SysRoleVo> convertToVoList(List<SysRole> roleList) {
        List<SysRoleVo> roleVoList = new ArrayList<>();
        for (SysRole sysRole : roleList) {
            roleVoList.add(convertToVo(sysRole));
        }
        return roleVoList;
    }
    
    private SysRoleVo convertToVo(SysRole sysRole) {
        SysRoleVo roleVo = new SysRoleVo();
        roleVo.setRoleId(sysRole.getRoleId());
        roleVo.setRoleName(sysRole.getRoleName());
        roleVo.setRoleKey(sysRole.getRoleKey());
        roleVo.setRoleSort(sysRole.getRoleSort());
        roleVo.setDataScope(sysRole.getDataScope());
        roleVo.setMenuCheckStrictly(sysRole.getMenuCheckStrictly());
        roleVo.setDeptCheckStrictly(sysRole.getDeptCheckStrictly());
        roleVo.setStatus(sysRole.getStatus());
        roleVo.setRemark(sysRole.getRemark());
        // 转换LocalDateTime到Date
        if (sysRole.getCreateTime() != null) {
            roleVo.setCreateTime(java.sql.Timestamp.valueOf(sysRole.getCreateTime()));
        }
        return roleVo;
    }

    // private Wrapper<SysRole> buildQueryWrapper(SysRoleBo bo) {
    //     Map<String, Object> params = bo.getParams();
    //     QueryWrapper<SysRole> wrapper = Wrappers.query();
    //     wrapper.eq("r.del_flag", SystemConstants.NORMAL)
    //         .eq(ObjectUtil.isNotNull(bo.getRoleId()), "r.role_id", bo.getRoleId())
    //         .like(StringUtils.isNotBlank(bo.getRoleName()), "r.role_name", bo.getRoleName())
    //         .eq(StringUtils.isNotBlank(bo.getStatus()), "r.status", bo.getStatus())
    //         .like(StringUtils.isNotBlank(bo.getRoleKey()), "r.role_key", bo.getRoleKey())
    //         .between(params.get("beginTime") != null && params.get("endTime") != null,
    //             "r.create_time", params.get("beginTime"), params.get("endTime"))
    //         .orderByAsc("r.role_sort").orderByAsc("r.create_time");
    //     return wrapper;
    // }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRolesByUserId(Long userId) {
        try {
            List<SysRole> roleList = baseMapper.selectRolesByUserId(userId);
            List<SysRoleVo> roleVoList = new ArrayList<>();
            for (SysRole sysRole : roleList) {
                SysRoleVo roleVo = new SysRoleVo();
                roleVo.setRoleId(sysRole.getRoleId());
                roleVo.setRoleName(sysRole.getRoleName());
                roleVo.setRoleKey(sysRole.getRoleKey());
                roleVo.setRoleSort(sysRole.getRoleSort());
                roleVo.setDataScope(sysRole.getDataScope());
                roleVo.setMenuCheckStrictly(sysRole.getMenuCheckStrictly());
                roleVo.setDeptCheckStrictly(sysRole.getDeptCheckStrictly());
                roleVo.setStatus(sysRole.getStatus());
                roleVo.setRemark(sysRole.getRemark());
                roleVo.setDelFlag(sysRole.getDelFlag());
                roleVo.setCreateTime(sysRole.getCreateTime() != null ? 
                    java.sql.Timestamp.valueOf(sysRole.getCreateTime()) : null);
                roleVoList.add(roleVo);
            }
            return roleVoList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据用户ID查询角色列表(包含被授权状态)
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRolesAuthByUserId(Long userId) {
        try {
            // 获取用户已分配的角色
            List<SysRole> userRoles = baseMapper.selectRolesByUserId(userId);
            Set<Long> userRoleIds = new HashSet<>();
            for (SysRole role : userRoles) {
                userRoleIds.add(role.getRoleId());
            }
            
            // 获取所有角色
            List<SysRoleVo> allRoles = selectRoleAll();
            
            // 标记用户已分配的角色
            for (SysRoleVo role : allRoles) {
                if (userRoleIds.contains(role.getRoleId())) {
                    role.setFlag(true);
                }
            }
            
            return allRoles;
        } catch (Exception e) {
            // 如果出错，返回空列表
            return new ArrayList<>();
        }
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<String> perms = baseMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.add(perm.trim());
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRoleAll() {
        return this.selectRoleList(new SysRoleBo());
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        // List<SysRoleVo> list = baseMapper.selectRolesByUserId(userId);
        // return StreamUtils.toList(list, SysRoleVo::getRoleId);
        // 暂时返回空列表，后续实现
        return new ArrayList<>();
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRoleVo selectRoleById(Long roleId) {
        return baseMapper.selectRoleById(roleId);
    }

    /**
     * 通过角色ID串查询角色
     *
     * @param roleIds 角色ID串
     * @return 角色列表信息
     */
    @Override
    public List<SysRoleVo> selectRoleByIds(List<Long> roleIds) {
        System.out.println("=== selectRoleByIds 被调用，roleIds: " + roleIds + " ===");
        
        // 如果roleIds为空，返回所有角色
        if (roleIds == null || roleIds.isEmpty()) {
            System.out.println("=== roleIds为空，返回所有角色 ===");
            return selectRoleAll();
        }
        
        // 根据roleIds查询指定角色
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0")
               .eq("status", "0")
               .in("role_id", roleIds)
               .orderByAsc("role_sort");
        
        List<SysRole> roleList = baseMapper.selectList(wrapper);
        System.out.println("=== 根据roleIds查询到角色数量: " + (roleList != null ? roleList.size() : "null") + " ===");
        return convertToVoList(roleList);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(SysRoleBo role) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name", role.getRoleName())
               .eq("del_flag", "0");
        if (role.getRoleId() != null) {
            wrapper.ne("role_id", role.getRoleId());
        }
        long count = baseMapper.selectCount(wrapper);
        return count == 0;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleKeyUnique(SysRoleBo role) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_key", role.getRoleKey())
               .eq("del_flag", "0");
        if (role.getRoleId() != null) {
            wrapper.ne("role_id", role.getRoleId());
        }
        long count = baseMapper.selectCount(wrapper);
        return count == 0;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRoleBo role) {
        // 暂时不做校验，后续实现
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        // 暂时不做校验，后续实现
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public long countUserRoleByRoleId(Long roleId) {
        // return userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
        // 暂时返回0，后续实现
        return 0;
    }

    /**
     * 新增保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Override
    public int insertRole(SysRoleBo bo) {
        SysRole role = new SysRole();
        role.setRoleName(bo.getRoleName());
        role.setRoleKey(bo.getRoleKey());
        role.setRoleSort(bo.getRoleSort());
        role.setDataScope(bo.getDataScope());
        role.setMenuCheckStrictly(bo.getMenuCheckStrictly());
        role.setDeptCheckStrictly(bo.getDeptCheckStrictly());
        role.setStatus(bo.getStatus());
        role.setRemark(bo.getRemark());
        role.setDelFlag("0");
        role.setCreateTime(LocalDateTime.now());
        
        return baseMapper.insert(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Override
    public int updateRole(SysRoleBo bo) {
        SysRole role = new SysRole();
        role.setRoleId(bo.getRoleId());
        role.setRoleName(bo.getRoleName());
        role.setRoleKey(bo.getRoleKey());
        role.setRoleSort(bo.getRoleSort());
        role.setDataScope(bo.getDataScope());
        role.setMenuCheckStrictly(bo.getMenuCheckStrictly());
        role.setDeptCheckStrictly(bo.getDeptCheckStrictly());
        role.setStatus(bo.getStatus());
        role.setRemark(bo.getRemark());
        role.setUpdateTime(LocalDateTime.now());
        
        return baseMapper.updateById(role);
    }

    /**
     * 修改角色状态
     *
     * @param roleId 角色ID
     * @param status 角色状态
     * @return 结果
     */
    @Override
    public int updateRoleStatus(Long roleId, String status) {
        // 暂时返回1，后续实现
        return 1;
    }

    /**
     * 修改数据权限信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Override
    // @Transactional(rollbackFor = Exception.class)
    public int authDataScope(SysRoleBo bo) {
        // 暂时返回1，后续实现
        return 1;
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    // @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Long roleId) {
        // 暂时返回1，后续实现
        return 1;
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    // @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            SysRoleBo role = new SysRoleBo();
            role.setRoleId(roleId);
            checkRoleAllowed(role);
            checkRoleDataScope(roleId);
            SysRoleVo sysRole = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new RuntimeException(String.format("%1$s已分配,不能删除", sysRole.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        // roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, Arrays.asList(roleIds)));
        // 删除角色与部门关联
        // roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().in(SysRoleDept::getRoleId, Arrays.asList(roleIds)));
        return baseMapper.deleteRoleByIds(roleIds);
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        if (roleId == null || userIds == null || userIds.length == 0) {
            return 0;
        }
        int rows = userRoleMapper.deleteByRoleIdAndUserIds(roleId, userIds);
        // 可根据需要清理在线用户缓存：cleanOnlineUser(Arrays.asList(userIds));
        return rows;
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        if (roleId == null || userIds == null || userIds.length == 0) {
            return 0;
        }
        // 防重复：先删除已存在的，再批量插入
        userRoleMapper.deleteByRoleIdAndUserIds(roleId, userIds);
        java.util.List<SysUserRole> list = new java.util.ArrayList<>(userIds.length);
        for (Long uid : userIds) {
            if (uid == null) continue;
            SysUserRole ur = new SysUserRole();
            ur.setUserId(uid);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        if (list.isEmpty()) {
            return 0;
        }
        int rows = userRoleMapper.insertBatch(list);
        // 可根据需要清理在线用户缓存：cleanOnlineUser(Arrays.asList(userIds));
        return rows;
    }

    @Override
    public void cleanOnlineUserByRole(Long roleId) {
        // 暂时不做处理，后续实现
    }

    @Override
    public void cleanOnlineUser(List<Long> userIds) {
        // 暂时不做处理，后续实现
    }
}