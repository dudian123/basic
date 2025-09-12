package org.example.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 简单菜单VO，用于测试JSON序列化
 */
@Data
public class SimpleMenuVo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 菜单ID
     */
    private Long menuId;
    
    /**
     * 菜单名称
     */
    private String menuName;
    
    /**
     * 菜单类型
     */
    private String menuType;
    
    /**
     * 无参构造函数
     */
    public SimpleMenuVo() {
    }
    
    /**
     * 构造函数
     */
    public SimpleMenuVo(Long menuId, String menuName, String menuType) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuType = menuType;
    }
}