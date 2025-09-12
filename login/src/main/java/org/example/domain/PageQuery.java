package org.example.domain;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询实体类
 *
 * @author Lion Li
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc;

    /**
     * 当前记录起始索引 默认值
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 每页显示记录数 默认值 默认查全部
     */
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;

    public Integer getPageNum() {
        return pageNum == null ? DEFAULT_PAGE_NUM : pageNum;
    }

    public Integer getPageSize() {
        return pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public String getOrderBy() {
        if (StringUtils.isEmpty(orderByColumn)) {
            return "";
        }
        return toUnderScoreCase(orderByColumn) + " " + ("asc".equals(isAsc) ? "asc" : "desc");
    }

    public <T> Page<T> build() {
        Integer pageNum = getPageNum();
        Integer pageSize = getPageSize();
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        Page<T> page = new Page<>(pageNum, pageSize);
        List<OrderItem> orderItems = buildOrderItem();
        if (!orderItems.isEmpty()) {
            page.addOrder(orderItems);
        }
        return page;
    }

    /**
     * 构建排序
     *
     * @return 排序字段
     */
    private List<OrderItem> buildOrderItem() {
        List<OrderItem> orderItems = new ArrayList<>();
        if (StringUtils.isNotEmpty(orderByColumn)) {
            String orderBy = toUnderScoreCase(orderByColumn);
            if ("asc".equals(isAsc)) {
                orderItems.add(OrderItem.asc(orderBy));
            } else {
                orderItems.add(OrderItem.desc(orderBy));
            }
        }
        return orderItems;
    }

    /**
     * 驼峰转下划线
     */
    private String toUnderScoreCase(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

}