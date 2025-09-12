package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.SysMenu;
import org.example.domain.bo.SysMenuBo;
import org.example.domain.vo.SysMenuVo;
import org.example.domain.vo.SimpleMenuVo;
import org.example.domain.vo.RouterVo;
import org.example.service.ISysMenuService;
import org.example.utils.LoginHelper;
import org.example.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 菜单信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/menu")
public class SysMenuController /* extends BaseController */ {

    private final ISysMenuService menuService;

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public Object getRouters() {
        Long userId = LoginHelper.getUserId();
        if (userId == null) {
            userId = 1L; // 默认管理员用户ID
        }
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        List<RouterVo> routers = menuService.buildMenus(menus);
        
        // 简单的成功响应格式
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("code", 200);
        result.put("msg", "操作成功");
        result.put("data", routers);
        return result;
    }

    /**
     * 获取菜单列表
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<Map<String, Object>> list(SysMenuBo menu) {
        try {
            Long userId = LoginHelper.getUserId();
            if (userId == null) {
                userId = 1L; // 默认用户ID
            }
            Map<String, Object> result = menuService.selectMenuListWithPage(menu, userId);
            return R.ok(result);
        } catch (Exception e) {
            return R.fail("获取菜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据菜单编号获取详细信息
     *
     * @param menuId 菜单ID
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<SysMenuVo> getInfo(@PathVariable Long menuId) {
        try {
            SysMenuVo menu = menuService.selectMenuById(menuId);
            return R.ok(menu);
        } catch (Exception e) {
            return R.fail("获取菜单信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜单下拉树列表
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/treeselect")
    public R<List<Object>> treeselect(SysMenuBo menu) {
        try {
            Long userId = LoginHelper.getUserId();
            if (userId == null) {
                userId = 1L; // 默认管理员用户ID
            }
            List<SimpleMenuVo> simpleMenus = menuService.selectMenuList(menu, userId);
            // 转换为SysMenuVo用于构建树形结构
            List<SysMenuVo> menus = new ArrayList<>();
            for (SimpleMenuVo simpleMenu : simpleMenus) {
                SysMenuVo menuVo = new SysMenuVo();
                menuVo.setMenuId(simpleMenu.getMenuId());
                menuVo.setMenuName(simpleMenu.getMenuName());
                menuVo.setMenuType(simpleMenu.getMenuType());
                menus.add(menuVo);
            }
            List<Object> treeSelect = menuService.buildMenuTreeSelect(menus);
            return R.ok(treeSelect);
        } catch (Exception e) {
            return R.fail("获取菜单树列表失败: " + e.getMessage());
        }
    }

    /**
     * 加载对应角色菜单列表树
     *
     * @param roleId 角色ID
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public Object roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        // List<SysMenuVo> menus = menuService.selectMenuList(LoginHelper.getUserId());
        // MenuTreeSelectVo selectVo = new MenuTreeSelectVo();
        // selectVo.checkedKeys = menuService.selectMenuListByRoleId(roleId);
        // selectVo.menus = menuService.buildMenuTreeSelect(menus);
        // return R.ok(selectVo);
        return null;
    }

    /**
     * 加载对应租户套餐菜单列表树
     *
     * @param packageId 租户套餐ID
     */
    @GetMapping(value = "/tenantPackageMenuTreeselect/{packageId}")
    public Object tenantPackageMenuTreeselect(@PathVariable("packageId") Long packageId) {
        // List<SysMenuVo> menus = menuService.selectMenuList(LoginHelper.getUserId());
        // MenuTreeSelectVo selectVo = new MenuTreeSelectVo();
        // selectVo.checkedKeys = menuService.selectMenuListByPackageId(packageId);
        // selectVo.menus = menuService.buildMenuTreeSelect(menus);
        // return R.ok(selectVo);
        return null;
    }

    /**
     * 新增菜单
     */
    @SaCheckPermission("system:menu:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysMenuBo menu) {
        try {
            if (!menuService.checkMenuNameUnique(menu)) {
                return R.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
            }
            int result = menuService.insertMenu(menu);
            return result > 0 ? R.ok() : R.fail("新增菜单失败");
        } catch (Exception e) {
            return R.fail("新增菜单失败: " + e.getMessage());
        }
    }

    /**
     * 修改菜单
     */
    @SaCheckPermission("system:menu:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysMenuBo menu) {
        try {
            if (!menuService.checkMenuNameUnique(menu)) {
                return R.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
            } else if (menu.getMenuId().equals(menu.getParentId())) {
                return R.fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
            }
            int result = menuService.updateMenu(menu);
            return result > 0 ? R.ok() : R.fail("修改菜单失败");
        } catch (Exception e) {
            return R.fail("修改菜单失败: " + e.getMessage());
        }
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     */
    @SaCheckPermission("system:menu:remove")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable("menuId") Long menuId) {
        try {
            if (menuService.hasChildByMenuId(menuId)) {
                return R.fail("存在子菜单,不允许删除");
            }
            if (menuService.checkMenuExistRole(menuId)) {
                return R.fail("菜单已分配,不允许删除");
            }
            int result = menuService.deleteMenuById(menuId);
            return result > 0 ? R.ok() : R.fail("删除菜单失败");
        } catch (Exception e) {
            return R.fail("删除菜单失败: " + e.getMessage());
        }
    }

    public record MenuTreeSelectVo(List<Long> checkedKeys, List<Object> menus) {
    }

    /**
     * 批量级联删除菜单
     *
     * @param menuIds 菜单ID串
     */
    @SaCheckPermission("system:menu:remove")
    @DeleteMapping("/cascade/{menuIds}")
    public R<Void> removeCascade(@PathVariable("menuIds") Long[] menuIds) {
        try {
            List<Long> menuIdList = List.of(menuIds);
            if (menuService.hasChildByMenuId(menuIdList)) {
                return R.fail("存在子菜单,不允许删除");
            }
            menuService.deleteMenuById(menuIdList);
            return R.ok();
        } catch (Exception e) {
            return R.fail("批量删除菜单失败: " + e.getMessage());
        }
    }

}