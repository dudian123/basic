package org.example.service;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.vo.SysRoleVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义权限验证接口扩展
 * 实现基于角色的访问控制(RBAC)机制
 * 从真实数据库获取用户角色和权限信息
 *
 * @author example
 */
@Service
@Slf4j
public class SaPermissionImpl implements StpInterface {
    
    private final ISysRoleService roleService;
    private final ISysMenuService menuService;
    
    public SaPermissionImpl(ISysRoleService roleService, ISysMenuService menuService) {
        this.roleService = roleService;
        this.menuService = menuService;
        System.err.println("=== SaPermissionImpl 构造函数被调用 ===");
        log.error("=== SaPermissionImpl 构造函数被调用 ===");
    }

    /**
     * 返回一个账号所拥有的权限码集合
     * 从数据库查询用户的菜单权限
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 检查loginId是否有效
        if (loginId == null || StringUtils.isBlank(loginId.toString())) {
            log.warn("getPermissionList: loginId is null or blank, returning default permissions.");
            return getDefaultPermissions();
        }

        try {
            // 解析loginId获取用户ID
            Long userId = Long.parseLong(loginId.toString());
            log.info("获取用户 {} 的权限列表", userId);
            
            // 从数据库查询权限（服务层返回Set）
            Set<String> permsSet = menuService.selectMenuPermsByUserId(userId);
            
            // 如果权限为空，则返回默认权限
            if (permsSet == null || permsSet.isEmpty()) {
                log.warn("用户 {} 的权限列表为空，返回默认权限", userId);
                return getDefaultPermissions();
            }
            
            log.info("用户 {} 的权限列表获取成功，共 {} 条", userId, permsSet.size());
            return new ArrayList<>(permsSet);
        } catch (NumberFormatException e) {
            log.error("getPermissionList: Invalid loginId format: {}", loginId, e);
            return getDefaultPermissions();
        } catch (Exception e) {
            log.error("获取用户权限列表时发生异常", e);
            return getDefaultPermissions();
        }
    }

    /**
     * 获取角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 检查loginId是否有效
        if (loginId == null || StringUtils.isBlank(loginId.toString())) {
            log.warn("getRoleList: loginId is null or blank, returning default roles.");
            return getDefaultRoles();
        }

        try {
            // 解析loginId获取用户ID
            Long userId = Long.parseLong(loginId.toString());
            log.info("获取用户 {} 的角色列表", userId);
            
            // 从数据库查询角色（服务层返回Set）
            Set<String> rolesSet = roleService.selectRolePermissionByUserId(userId);
            
            // 如果角色为空，则返回默认角色
            if (rolesSet == null || rolesSet.isEmpty()) {
                log.warn("用户 {} 的角色列表为空，返回默认角色", userId);
                return getDefaultRoles();
            }
            
            log.info("用户 {} 的角色列表获取成功，共 {} 条", userId, rolesSet.size());
            return new ArrayList<>(rolesSet);
        } catch (NumberFormatException e) {
            log.error("getRoleList: Invalid loginId format: {}", loginId, e);
            return getDefaultRoles();
        } catch (Exception e) {
            log.error("获取用户角色列表时发生异常", e);
            return getDefaultRoles();
        }
    }

    /**
     * 获取默认权限列表（当用户无任何权限时返回）
     */
    public List<String> getDefaultPermissions() {
        List<String> defaultPermissions = new ArrayList<>();
        defaultPermissions.add("system:config:list");
        defaultPermissions.add("system:config:query");
        defaultPermissions.add("system:config:add");
        defaultPermissions.add("system:config:edit");
        defaultPermissions.add("system:config:remove");
        defaultPermissions.add("system:dict:list");
        defaultPermissions.add("system:dict:query");
        defaultPermissions.add("system:dict:add");
        defaultPermissions.add("system:dict:edit");
        defaultPermissions.add("system:dict:remove");
        defaultPermissions.add("system:dictData:list");
        defaultPermissions.add("system:dictData:query");
        defaultPermissions.add("system:dictData:add");
        defaultPermissions.add("system:dictData:edit");
        defaultPermissions.add("system:dictData:remove");
        defaultPermissions.add("system:user:list");
        defaultPermissions.add("system:user:query");
        defaultPermissions.add("system:user:add");
        defaultPermissions.add("system:user:edit");
        defaultPermissions.add("system:user:remove");
        defaultPermissions.add("system:role:list");
        defaultPermissions.add("system:role:query");
        defaultPermissions.add("system:role:add");
        defaultPermissions.add("system:role:edit");
        defaultPermissions.add("system:role:remove");
        defaultPermissions.add("system:menu:list");
        defaultPermissions.add("system:menu:query");
        defaultPermissions.add("system:menu:add");
        defaultPermissions.add("system:menu:edit");
        defaultPermissions.add("system:menu:remove");
        return defaultPermissions;
    }
    
    /**
     * 获取用户默认角色
     */
    public List<String> getDefaultRoles() {
        List<String> defaultRoles = new ArrayList<>();
        defaultRoles.add("common");
        return defaultRoles;
    }

    /**
     * 获取用户默认角色（兼容性处理）
     * 当数据库中没有角色数据时使用
     */
    private List<String> getDefaultRolesByUserId(Long userId) {
        List<String> defaultRoles = new ArrayList<>();
        
        // 根据用户ID分配默认角色
        if (userId != null) {
            if (userId.equals(1L)) {
                // ID为1的用户默认为超级管理员
                defaultRoles.add("admin");
            } else {
                // 其他用户默认为普通用户
                defaultRoles.add("common");
            }
        }
        
        return defaultRoles;
    }
    
    /**
     * 获取用户默认权限（兼容性处理）
     * 当数据库中没有权限数据时使用
     */
    private List<String> getDefaultPermissionsByUserId(Long userId) {
        Set<String> defaultPermissions = new HashSet<>();
        
        if (userId != null) {
            if (userId.equals(1L)) {
                // ID为1的用户默认拥有所有权限
                defaultPermissions.addAll(getAllSystemPermissions());
            } else {
                // 其他用户默认拥有基本权限
                defaultPermissions.addAll(getBasicPermissions());
            }
        }
        
        return new ArrayList<>(defaultPermissions);
    }
    
    /**
     * 获取系统所有权限（用于超级管理员）
     */
    private List<String> getAllSystemPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:config:list");
        permissions.add("system:config:query");
        permissions.add("system:config:add");
        permissions.add("system:config:edit");
        permissions.add("system:config:remove");
        permissions.add("system:dict:list");
        permissions.add("system:dict:query");
        permissions.add("system:dict:add");
        permissions.add("system:dict:edit");
        permissions.add("system:dict:remove");
        permissions.add("system:dictData:list");
        permissions.add("system:dictData:query");
        permissions.add("system:dictData:add");
        permissions.add("system:dictData:edit");
        permissions.add("system:dictData:remove");
        permissions.add("system:user:list");
        permissions.add("system:user:query");
        permissions.add("system:user:add");
        permissions.add("system:user:edit");
        permissions.add("system:user:remove");
        permissions.add("system:role:list");
        permissions.add("system:role:query");
        permissions.add("system:role:add");
        permissions.add("system:role:edit");
        permissions.add("system:role:remove");
        permissions.add("system:menu:list");
        permissions.add("system:menu:query");
        permissions.add("system:menu:add");
        permissions.add("system:menu:edit");
        permissions.add("system:menu:remove");
        return new ArrayList<>(permissions);
    }
    
    /**
     * 获取基本权限（用于普通用户）
     */
    private List<String> getBasicPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:dict:query");
        permissions.add("system:dictData:query");
        permissions.add("system:user:query");
        return new ArrayList<>(permissions);
    }
}