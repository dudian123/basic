package org.example.domain.entity;

// import com.baomidou.mybatisplus.annotation.TableField;
// import com.baomidou.mybatisplus.annotation.TableId;
// import com.baomidou.mybatisplus.annotation.TableName;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import org.dromara.common.core.constant.Constants;
// import org.dromara.common.core.constant.SystemConstants;
// import org.dromara.common.core.utils.StringUtils;
import org.example.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 *
 * @author Lion Li
 */

// @Data
// @EqualsAndHashCode(callSuper = true)
// @TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /**
     * 菜单ID
     */
    // @TableId(value = "menu_id")
    private Long menuId;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String queryParam;

    /**
     * 是否为外链（0是 1否）
     */
    private String isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private String isCache;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 显示状态（0显示 1隐藏）
     */
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    private String status;

    /**
     * 权限字符串
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子菜单
     */
    // @TableField(exist = false) - 注释掉因为没有MyBatis Plus依赖
    private transient List<SysMenu> children;

    /**
     * 获取路由名称
     */
    public String getRouteName() {
        // String routerName = StringUtils.capitalize(path);
        String routerName = path != null ? path.substring(0, 1).toUpperCase() + path.substring(1) : "";
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame()) {
            routerName = "";
        }
        return routerName;
    }

    /**
     * 获取路由地址
     */
    public String getRouterPath() {
        String routerPath = this.path;
        // 内链打开外网方式
        if (getParentId() != 0L && isInnerLink()) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0L == getParentId() && "M".equals(getMenuType())
            && "1".equals(getIsFrame())) {
            routerPath = "/" + this.path;
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame()) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     */
    public String getComponentInfo() {
        String component = "Layout";
        if (this.component != null && !this.component.isEmpty() && !isMenuFrame()) {
            component = this.component;
        } else if ((this.component == null || this.component.isEmpty()) && getParentId() != 0L && isInnerLink()) {
            component = "InnerLink";
        } else if ((this.component == null || this.component.isEmpty()) && isParentView()) {
            component = "ParentView";
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     */
    public boolean isMenuFrame() {
        return getParentId() == 0L && "C".equals(menuType) && "1".equals(isFrame);
    }

    /**
     * 是否为内链组件
     */
    public boolean isInnerLink() {
        return "1".equals(isFrame) && isHttp(path);
    }

    /**
     * 是否为parent_view组件
     */
    public boolean isParentView() {
        return getParentId() != 0L && "M".equals(menuType);
    }

    /**
     * 内链域名特殊字符替换
     */
    public static String innerLinkReplaceEach(String path) {
        if (path == null) return "";
        return path.replace("http://", "")
                  .replace("https://", "")
                  .replace("www.", "")
                  .replace(".", "/")
                  .replace(":", "/");
    }

    /**
     * 判断是否为http链接
     */
    private boolean isHttp(String link) {
        return link != null && (link.startsWith("http://") || link.startsWith("https://"));
    }

    // Getters and Setters
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    public String getIsFrame() {
        return isFrame;
    }

    public void setIsFrame(String isFrame) {
        this.isFrame = isFrame;
    }

    public String getIsCache() {
        return isCache;
    }

    public void setIsCache(String isCache) {
        this.isCache = isCache;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }



    public List<SysMenu> getChildren() {
        return children;
    }

    public void setChildren(List<SysMenu> children) {
        this.children = children;
    }
}