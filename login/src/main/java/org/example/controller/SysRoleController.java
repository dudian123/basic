package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.example.domain.bo.SysRoleBo;
import org.example.domain.vo.SysRoleVo;
import org.example.domain.vo.SysUserVo;
import org.example.domain.PageQuery;
import org.example.domain.TableDataInfo;
import org.example.service.ISysRoleService;
import org.example.service.ISysUserService;
import org.example.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色信息
 *
 * @author Lion Li
 */
@Validated
// @RequiredArgsConstructor
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final ISysRoleService roleService;
    private final ISysUserService userService;
    // private final ISysDeptService deptService;

    public SysRoleController(ISysRoleService roleService, ISysUserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    /**
     * 获取角色信息列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    public TableDataInfo<SysRoleVo> list(SysRoleBo role, PageQuery pageQuery) {
        System.err.println("=== RoleController.list 方法被调用，分页参数: " + pageQuery + " ===");
        try {
            TableDataInfo<SysRoleVo> result = roleService.selectPageRoleList(role, pageQuery);
            System.err.println("=== RoleController.list 获取到分页结果，总数: " + result.getTotal() + ", 当前页数据量: " + result.getRows().size() + " ===");
            return result;
        } catch (Exception e) {
            System.err.println("=== RoleController.list 发生异常: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 角色管理页面入口
     */
    @GetMapping({"/", ""})
    public R<Map<String, Object>> index() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "角色管理页面");
        return R.ok(data);
    }

    /**
     * 导出角色信息列表
     */
    // @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    // @SaCheckPermission("system:role:export")
    // @PostMapping("/export")
    // public void export(SysRoleBo role, HttpServletResponse response) {
    //     List<SysRoleVo> list = roleService.selectRoleList(role);
    //     ExcelUtil.exportExcel(list, "角色数据", SysRoleVo.class, response);
    // }

    /**
     * 根据角色编号获取详细信息
     *
     * @param roleId 角色ID
     */
    @SaCheckPermission("system:role:query")
    @GetMapping(value = "/{roleId}")
    public R<SysRoleVo> getInfo(@PathVariable Long roleId) {
        try {
            roleService.checkRoleDataScope(roleId);
            SysRoleVo roleVo = roleService.selectRoleById(roleId);
            return R.ok(roleVo);
        } catch (Exception e) {
            return R.fail("获取角色信息失败: " + e.getMessage());
        }
    }

    /**
     * 新增角色
     */
    @SaCheckPermission("system:role:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysRoleBo role) {
        try {
            roleService.checkRoleAllowed(role);
            if (!roleService.checkRoleNameUnique(role)) {
                return R.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
            } else if (!roleService.checkRoleKeyUnique(role)) {
                return R.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
            }
            int result = roleService.insertRole(role);
            return result > 0 ? R.ok() : R.fail("新增角色失败");
        } catch (Exception e) {
            return R.fail("新增角色失败: " + e.getMessage());
        }
    }

    /**
     * 修改保存角色
     */
    @SaCheckPermission("system:role:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysRoleBo role) {
        try {
            roleService.checkRoleAllowed(role);
            roleService.checkRoleDataScope(role.getRoleId());
            if (!roleService.checkRoleNameUnique(role)) {
                return R.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
            } else if (!roleService.checkRoleKeyUnique(role)) {
                return R.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
            }

            if (roleService.updateRole(role) > 0) {
                roleService.cleanOnlineUserByRole(role.getRoleId());
                return R.ok();
            }
            return R.fail("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
        } catch (Exception e) {
            return R.fail("修改角色失败: " + e.getMessage());
        }
    }

    /**
     * 修改保存数据权限
     */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/dataScope")
    public R<Void> dataScope(@RequestBody SysRoleBo role) {
        try {
            roleService.checkRoleAllowed(role);
            roleService.checkRoleDataScope(role.getRoleId());
            int result = roleService.authDataScope(role);
            return result > 0 ? R.ok() : R.fail("数据权限设置失败");
        } catch (Exception e) {
            return R.fail("数据权限设置失败: " + e.getMessage());
        }
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysRoleBo role) {
        try {
            roleService.checkRoleAllowed(role);
            roleService.checkRoleDataScope(role.getRoleId());
            int result = roleService.updateRoleStatus(role.getRoleId(), role.getStatus());
            return result > 0 ? R.ok() : R.fail("状态修改失败");
        } catch (Exception e) {
            return R.fail("状态修改失败: " + e.getMessage());
        }
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:role:remove")
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        try {
            int result = roleService.deleteRoleByIds(roleIds);
            return result > 0 ? R.ok() : R.fail("删除角色失败");
        } catch (Exception e) {
            return R.fail("删除角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色选择框列表
     *
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/optionselect")
    public R<List<SysRoleVo>> optionselect(@RequestParam(required = false) Long[] roleIds) {
        try {
            List<SysRoleVo> list = roleService.selectRoleByIds(roleIds == null ? null : List.of(roleIds));
            return R.ok(list);
        } catch (Exception e) {
            return R.fail("获取角色选择框列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询已分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo<SysUserVo> allocatedList(Long roleId, String userName, String phonenumber, PageQuery pageQuery) {
        System.err.println("=== RoleController.allocatedList 方法被调用，角色ID: " + roleId + ", 分页参数: " + pageQuery + " ===");
        try {
            if (roleId == null) {
                throw new RuntimeException("角色ID不能为空");
            }
            TableDataInfo<SysUserVo> result = userService.selectAllocatedList(roleId, userName, phonenumber, pageQuery);
            System.err.println("=== RoleController.allocatedList 获取到分页结果，总数: " + result.getTotal() + ", 当前页数据量: " + result.getRows().size() + " ===");
            return result;
        } catch (Exception e) {
            System.err.println("=== RoleController.allocatedList 发生异常: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 查询未分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo<SysUserVo> unallocatedList(Long roleId, String userName, String phonenumber, PageQuery pageQuery) {
        System.err.println("=== RoleController.unallocatedList 方法被调用，角色ID: " + roleId + ", 分页参数: " + pageQuery + " ===");
        try {
            if (roleId == null) {
                throw new RuntimeException("角色ID不能为空");
            }
            TableDataInfo<SysUserVo> result = userService.selectUnallocatedList(roleId, userName, phonenumber, pageQuery);
            System.err.println("=== RoleController.unallocatedList 获取到分页结果，总数: " + result.getTotal() + ", 当前页数据量: " + result.getRows().size() + " ===");
            return result;
        } catch (Exception e) {
            System.err.println("=== RoleController.unallocatedList 发生异常: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/cancel")
    public R<Void> cancelAuthUser(Long userId, Long roleId) {
        try {
            roleService.checkRoleDataScope(roleId);
            Long[] ids = (userId == null) ? new Long[0] : new Long[]{userId};
            int result = roleService.deleteAuthUsers(roleId, ids);
            return result > 0 ? R.ok() : R.fail("取消授权失败");
        } catch (Exception e) {
            return R.fail("取消授权失败: " + e.getMessage());
        }
    }

    /**
     * 批量取消授权用户
     *
     * @param roleId  角色ID
     * @param userIds 用户ID串
     */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/cancelAll")
    public R<Void> cancelAuthUserAll(Long roleId, String userIds) {
        try {
            roleService.checkRoleDataScope(roleId);
            Long[] ids;
            if (userIds == null || userIds.trim().isEmpty()) {
                ids = new Long[0];
            } else {
                String[] parts = userIds.split(",");
                ids = new Long[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    ids[i] = Long.valueOf(parts[i].trim());
                }
            }
            int result = roleService.deleteAuthUsers(roleId, ids);
            return result > 0 ? R.ok() : R.fail("批量取消授权失败");
        } catch (Exception e) {
            return R.fail("批量取消授权失败: " + e.getMessage());
        }
    }

    /**
     * 批量选择用户授权
     *
     * @param roleId  角色ID
     * @param userIds 用户ID串
     */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/selectAll")
    public R<Void> selectAuthUserAll(Long roleId, String userIds) {
        try {
            roleService.checkRoleDataScope(roleId);
            Long[] ids;
            if (userIds == null || userIds.trim().isEmpty()) {
                ids = new Long[0];
            } else {
                String[] parts = userIds.split(",");
                ids = new Long[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    ids[i] = Long.valueOf(parts[i].trim());
                }
            }
            int result = roleService.insertAuthUsers(roleId, ids);
            return result > 0 ? R.ok() : R.fail("用户授权失败");
        } catch (Exception e) {
            return R.fail("用户授权失败: " + e.getMessage());
        }
    }

    /**
     * 获取对应角色部门树列表
     *
     * @param roleId 角色ID
     */
    // @SaCheckPermission("system:role:list")
    // @GetMapping(value = "/deptTree/{roleId}")
    // public R<DeptTreeSelectVo> roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
    //     DeptTreeSelectVo selectVo = new DeptTreeSelectVo(
    //         deptService.selectDeptListByRoleId(roleId),
    //         deptService.selectDeptTreeList(new SysDeptBo()));
    //     return R.ok(selectVo);
    // }

    // public record DeptTreeSelectVo(List<Long> checkedKeys, List<Tree<Long>> depts) {}

}