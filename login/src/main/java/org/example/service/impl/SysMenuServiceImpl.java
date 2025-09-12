package org.example.service.impl;

// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.core.convert.Convert;
// import cn.hutool.core.lang.tree.Tree;
// import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.example.constant.Constants;
import org.example.utils.StringUtils;
import org.example.utils.MapstructUtils;
import org.example.utils.LoginHelper;
import cn.hutool.core.util.ObjectUtil;
import org.example.domain.entity.SysMenu;
import org.example.domain.entity.SysRole;
import org.example.domain.entity.SysRoleMenu;
import org.example.domain.bo.SysMenuBo;
import org.example.domain.vo.MetaVo;
import org.example.domain.vo.RouterVo;
import org.example.domain.vo.SysMenuVo;
import org.example.domain.vo.SimpleMenuVo;
import org.example.mapper.SysMenuMapper;
import org.example.mapper.SysRoleMapper;
import org.example.mapper.SysRoleMenuMapper;
// import org.example.mapper.SysTenantPackageMapper;
import org.example.service.ISysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.HashMap;

/**
 * 菜单 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    private final SysMenuMapper baseMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    // private final SysTenantPackageMapper tenantPackageMapper;

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuVo> selectMenuList(Long userId) {
        System.out.println("=== selectMenuList 开始 ===");
        System.out.println("userId: " + userId);
        System.out.println("isSuperAdmin: " + LoginHelper.isSuperAdmin(userId));
        
        List<SysMenuVo> menuList;
        // 管理员显示所有菜单信息
        if (LoginHelper.isSuperAdmin(userId)) {
            System.out.println("执行超级管理员查询路径");
            // 超级管理员查询所有菜单
            List<SysMenu> allMenus = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum));
            System.out.println("查询到的菜单数量: " + allMenus.size());
            if (allMenus.size() > 0) {
                System.out.println("第一个菜单: " + allMenus.get(0).getMenuName());
            }
            menuList = MapstructUtils.convert(allMenus, SysMenuVo.class);
        } else {
            System.out.println("执行普通用户查询路径");
            // 普通用户根据角色权限查询菜单
            List<SysMenu> userMenus = baseMapper.selectMenuTreeByUserId(userId);
            System.out.println("用户菜单数量: " + userMenus.size());
            menuList = MapstructUtils.convert(userMenus, SysMenuVo.class);
        }
        System.out.println("最终返回菜单数量: " + menuList.size());
        System.out.println("=== selectMenuList 结束 ===");
        return menuList;
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SimpleMenuVo> selectMenuList(SysMenuBo menu, Long userId) {
        System.out.println("=== selectMenuList 开始 ===");
        System.out.println("userId: " + userId);
        System.out.println("isSuperAdmin: " + LoginHelper.isSuperAdmin(userId));
        System.out.println("menu参数: menuName=" + menu.getMenuName() + ", visible=" + menu.getVisible() + ", status=" + menu.getStatus() + ", menuType=" + menu.getMenuType() + ", parentId=" + menu.getParentId());
        
        List<SysMenuVo> menuList;
        // 管理员显示所有菜单信息
        if (LoginHelper.isSuperAdmin(userId)) {
            System.out.println("执行超级管理员查询路径");
            // 超级管理员查询所有菜单
            List<SysMenu> allMenus = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .like(StringUtils.isNotBlank(menu.getMenuName()), SysMenu::getMenuName, menu.getMenuName())
                .eq(StringUtils.isNotBlank(menu.getVisible()), SysMenu::getVisible, menu.getVisible())
                .eq(StringUtils.isNotBlank(menu.getStatus()), SysMenu::getStatus, menu.getStatus())
                .eq(StringUtils.isNotBlank(menu.getMenuType()), SysMenu::getMenuType, menu.getMenuType())
                .eq(ObjectUtil.isNotNull(menu.getParentId()), SysMenu::getParentId, menu.getParentId())
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum));
            System.out.println("查询到的菜单数量: " + allMenus.size());
            if (allMenus.size() > 0) {
                System.out.println("第一个菜单: " + allMenus.get(0).getMenuName());
            }
            menuList = MapstructUtils.convert(allMenus, SysMenuVo.class);
        } else {
            System.out.println("执行普通用户查询路径");
            // 普通用户根据角色权限查询菜单
            List<SysMenu> userMenus = baseMapper.selectMenuTreeByUserId(userId);
            System.out.println("用户菜单数量: " + userMenus.size());
            menuList = MapstructUtils.convert(userMenus, SysMenuVo.class);
        }
        System.out.println("最终返回菜单数量: " + menuList.size());
        System.out.println("=== selectMenuList 结束 ===");
        // 测试：使用SimpleMenuVo
        List<SimpleMenuVo> simpleMenus = new ArrayList<>();
        for (SysMenuVo menuVo : menuList) {
            SimpleMenuVo simpleMenu = new SimpleMenuVo();
            simpleMenu.setMenuId(menuVo.getMenuId());
            simpleMenu.setMenuName(menuVo.getMenuName());
            simpleMenu.setMenuType(menuVo.getMenuType());
            simpleMenus.add(simpleMenu);
        }
        System.out.println("转换后的简单菜单数量: " + simpleMenus.size());
        return simpleMenus;
    }

    /**
     * 根据用户查询系统菜单列表（分页）
     *
     * @param menu   菜单信息
     * @param userId 用户ID
     * @return 分页结果
     */
    @Override
    public Map<String, Object> selectMenuListWithPage(SysMenuBo menu, Long userId) {
        System.out.println("=== selectMenuListWithPage 开始 ===");
        System.out.println("userId: " + userId);
        System.out.println("pageNum: " + menu.getPageNum() + ", pageSize: " + menu.getPageSize());
        
        List<SysMenuVo> allMenus;
        // 管理员显示所有菜单信息
        if (LoginHelper.isSuperAdmin(userId)) {
            System.out.println("执行超级管理员查询路径");
            // 超级管理员查询所有菜单
            List<SysMenu> menuEntities = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .like(StringUtils.isNotBlank(menu.getMenuName()), SysMenu::getMenuName, menu.getMenuName())
                .eq(StringUtils.isNotBlank(menu.getVisible()), SysMenu::getVisible, menu.getVisible())
                .eq(StringUtils.isNotBlank(menu.getStatus()), SysMenu::getStatus, menu.getStatus())
                .eq(StringUtils.isNotBlank(menu.getMenuType()), SysMenu::getMenuType, menu.getMenuType())
                .eq(ObjectUtil.isNotNull(menu.getParentId()), SysMenu::getParentId, menu.getParentId())
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum));
            allMenus = MapstructUtils.convert(menuEntities, SysMenuVo.class);
        } else {
            System.out.println("执行普通用户查询路径");
            // 普通用户根据角色权限查询菜单
            List<SysMenu> userMenus = baseMapper.selectMenuTreeByUserId(userId);
            allMenus = MapstructUtils.convert(userMenus, SysMenuVo.class);
        }
        
        // 计算分页
        int total = allMenus.size();
        int pageNum = menu.getPageNum() != null ? menu.getPageNum() : 1;
        int pageSize = menu.getPageSize() != null ? menu.getPageSize() : 10;
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<SysMenuVo> pagedMenus = new ArrayList<>();
        if (startIndex < total) {
            pagedMenus = allMenus.subList(startIndex, endIndex);
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("rows", pagedMenus);
        result.put("total", total);
        
        System.out.println("总记录数: " + total + ", 当前页: " + pageNum + ", 每页: " + pageSize);
        System.out.println("返回记录数: " + pagedMenus.size());
        System.out.println("=== selectMenuListWithPage 结束 ===");
        
        return result;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = baseMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(StringUtils.splitList(perm.trim()));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        List<String> perms = baseMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(StringUtils.splitList(perm.trim()));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus;
        if (LoginHelper.isSuperAdmin(userId)) {
            // 超级管理员获取所有菜单
            menus = baseMapper.selectMenuTreeAll();
        } else {
            // 普通用户根据权限获取菜单
            menus = baseMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0L);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        // SysRole role = roleMapper.selectById(roleId);
        // return baseMapper.selectMenuListByRoleId(roleId, role.getMenuCheckStrictly());
        return new ArrayList<>();
    }

    /**
     * 根据租户套餐ID查询菜单树信息
     *
     * @param packageId 租户套餐ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByPackageId(Long packageId) {
        // SysTenantPackage tenantPackage = tenantPackageMapper.selectById(packageId);
        // List<Long> menuIds = StringUtils.splitTo(tenantPackage.getMenuIds(), Convert::toLong);
        // if (CollUtil.isEmpty(menuIds)) {
        //     return List.of();
        // }
        // List<Long> parentIds = null;
        // if (tenantPackage.getMenuCheckStrictly()) {
        //     parentIds = baseMapper.selectObjs(new LambdaQueryWrapper<SysMenu>()
        //         .select(SysMenu::getParentId)
        //         .in(SysMenu::getMenuId, menuIds), x -> {
        //         return Convert.toLong(x);
        //     });
        // }
        // return baseMapper.selectObjs(new LambdaQueryWrapper<SysMenu>()
        //     .in(SysMenu::getMenuId, menuIds)
        //     .notIn(CollUtil.isNotEmpty(parentIds), SysMenu::getMenuId, parentIds), x -> {
        //     return Convert.toLong(x);
        // });
        return new ArrayList<>();
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<Object> buildMenuTreeSelect(List<SysMenuVo> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }
        List<Object> treeSelect = new ArrayList<>();
        for (SysMenuVo menu : menus) {
            Map<String, Object> menuMap = new HashMap<>();
            menuMap.put("id", menu.getMenuId());
            menuMap.put("parentId", menu.getParentId());
            menuMap.put("name", menu.getMenuName());
            menuMap.put("weight", menu.getOrderNum());
            menuMap.put("menuType", menu.getMenuType());
            menuMap.put("icon", menu.getIcon());
            treeSelect.add(menuMap);
        }
        return treeSelect;
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenuVo selectMenuById(Long menuId) {
        // return baseMapper.selectVoById(menuId);
        return null;
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        // return baseMapper.exists(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
        return false;
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuIds 菜单ID串
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(List<Long> menuIds) {
        // return baseMapper.exists(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getParentId, menuIds).notIn(SysMenu::getMenuId, menuIds));
        return false;
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        // return roleMenuMapper.exists(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId));
        return false;
    }

    /**
     * 新增保存菜单信息
     *
     * @param bo 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenuBo bo) {
        // SysMenu menu = MapstructUtils.convert(bo, SysMenu.class);
        // return baseMapper.insert(menu);
        return 0;
    }

    /**
     * 修改保存菜单信息
     *
     * @param bo 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenuBo bo) {
        // SysMenu menu = MapstructUtils.convert(bo, SysMenu.class);
        // return baseMapper.updateById(menu);
        return 0;
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId) {
        // return baseMapper.deleteById(menuId);
        return 0;
    }

    /**
     * 批量删除菜单管理信息
     *
     * @param menuIds 菜单ID串
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuById(List<Long> menuIds) {
        // baseMapper.deleteByIds(menuIds);
        // roleMenuMapper.deleteByMenuIds(menuIds);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkMenuNameUnique(SysMenuBo menu) {
        // boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysMenu>()
        //     .eq(SysMenu::getMenuName, menu.getMenuName())
        //     .eq(SysMenu::getParentId, menu.getParentId())
        //     .ne(ObjectUtil.isNotNull(menu.getMenuId()), SysMenu::getMenuId, menu.getMenuId()));
        // return !exist;
        return true;
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQueryParam());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (cMenus != null && !cMenus.isEmpty() && "M".equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQueryParam());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = SysMenu.innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent("InnerLink");
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = SysMenu.innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && "M".equals(menu.getMenuType()) && "1".equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = "Layout";
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = "InnerLink";
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = "ParentView";
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId().intValue() == 0 && "C".equals(menu.getMenuType()) && "1".equals(menu.getIsFrame());
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenu menu) {
        return "1".equals(menu.getIsFrame()) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId().intValue() != 0 && "M".equals(menu.getMenuType());
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    private List<SysMenu> getChildPerms(List<SysMenu> list, Long parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        if (list != null) {
            for (SysMenu t : list) {
                // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
                if (t.getParentId().equals(parentId)) {
                    recursionFn(list, t);
                    returnList.add(t);
                }
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = new ArrayList<>();
        for (SysMenu n : list) {
            if (n.getParentId().equals(t.getMenuId())) {
                childList.add(n);
            }
        }
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            // 判断是否有子节点
            boolean hasChild = false;
            for (SysMenu n : list) {
                if (n.getParentId().equals(tChild.getMenuId())) {
                    hasChild = true;
                    break;
                }
            }
            if (hasChild) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 构建菜单树结构
     *
     * @param menuList 菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<SysMenuVo> buildMenuTree(List<SysMenuVo> menuList, Long parentId) {
        List<SysMenuVo> tree = new ArrayList<>();
        for (SysMenuVo menu : menuList) {
            if (parentId.equals(menu.getParentId())) {
                List<SysMenuVo> children = buildMenuTree(menuList, menu.getMenuId());
                menu.setChildren(children);
                tree.add(menu);
            }
        }
        return tree;
    }

}