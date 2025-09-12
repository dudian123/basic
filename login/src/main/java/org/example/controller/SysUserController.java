package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ArrayUtil;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.SysUser;
import org.example.domain.vo.SysUserInfoVo;
import org.example.domain.vo.SysUserVo;
import org.example.domain.vo.SysRoleVo;
import org.example.domain.PageQuery;
import org.example.domain.TableDataInfo;
import org.example.service.ISysUserService;
import org.example.service.ISysRoleService;
import org.example.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final ISysUserService userService;
    private final ISysRoleService roleService;

    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserVo> list(PageQuery pageQuery) {
        System.err.println("=== Controller.list 方法被调用，分页参数: " + pageQuery + " ===");
        try {
            TableDataInfo<SysUserVo> result = userService.selectUserList(pageQuery);
            System.err.println("=== Controller.list 获取到分页结果，总数: " + result.getTotal() + ", 当前页数据量: " + result.getRows().size() + " ===");
            return result;
        } catch (Exception e) {
            System.err.println("=== Controller.list 发生异常: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 用户管理页面入口
     */
    @GetMapping("/")
    public R<Map<String, Object>> index() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "用户管理页面");
        return R.ok(data);
    }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping(value = "/{userId}")
    public R<SysUserInfoVo> getInfo(@PathVariable Long userId) {
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        SysUser sysUser = userService.selectUserById(userId);
        // 转换为VO对象
        userInfoVo.setUser(SysUserVo.fromSysUser(sysUser));
        return R.ok(userInfoVo);
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysUser user) {
        if (!userService.checkUserNameUnique(user.getUserName())) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        boolean result = userService.registerUser(user);
        return result ? R.ok() : R.fail("新增用户失败，请联系管理员");
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysUser user) {
        boolean ok = userService.updateUser(user);
        return ok ? R.ok() : R.fail("修改用户失败，请联系管理员");
    }

    /**
     * 删除用户（支持批量，使用逗号分隔的ID）
     */
    @SaCheckPermission("system:user:remove")
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.isEmpty(userIds)) {
            return R.fail("用户ID不能为空");
        }
        int rows = userService.deleteUserByIds(userIds);
        return rows > 0 ? R.ok() : R.fail("删除用户失败，请联系管理员");
    }

    /**
     * 根据用户编号获取授权角色
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/authRole/{userId}")
    public R<Map<String, Object>> authRole(@PathVariable Long userId) {
        try {
            SysUserVo user = userService.selectUserVoById(userId);
            List<SysRoleVo> allRolesWithAuth = roleService.selectRolesAuthByUserId(userId);
            
            // 分离已分配和未分配的角色
            List<SysRoleVo> allocatedRoles = new ArrayList<>();
            List<SysRoleVo> unallocatedRoles = new ArrayList<>();
            
            for (SysRoleVo role : allRolesWithAuth) {
                if (role.isFlag()) {
                    allocatedRoles.add(role);
                } else {
                    unallocatedRoles.add(role);
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("roles", allocatedRoles);
            result.put("unallocatedRoles", unallocatedRoles);
            
            return R.ok(result);
        } catch (Exception e) {
            return R.fail("获取用户角色信息失败: " + e.getMessage());
        }
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户Id
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:user:edit")
    @PutMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
        userService.insertUserAuth(userId, roleIds);
        return R.ok();
    }
}