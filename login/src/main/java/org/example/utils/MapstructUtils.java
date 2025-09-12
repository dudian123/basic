package org.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapstruct 工具类
 * 简化版本，用于对象转换
 *
 * @author System
 */
public class MapstructUtils {

    /**
     * 将 T 类型对象，转换为 desc 类型的对象并返回
     * 简化实现：使用反射进行对象转换
     *
     * @param source 数据来源实体
     * @param desc   描述对象 转换后的对象
     * @return desc
     */
    public static <T, V> V convert(T source, Class<V> desc) {
        if (source == null) {
            return null;
        }
        if (desc == null) {
            return null;
        }
        
        try {
            // 创建目标对象实例
            V target = desc.getDeclaredConstructor().newInstance();
            
            // 使用Spring的BeanUtils进行属性复制
            org.springframework.beans.BeanUtils.copyProperties(source, target);
            
            return target;
        } catch (Exception e) {
            // 如果转换失败，返回null
            return null;
        }
    }

    /**
     * 将 T 类型的集合，转换为 desc 类型的集合并返回
     * 简化实现：使用反射进行对象转换
     *
     * @param sourceList 数据来源实体列表
     * @param desc       描述对象 转换后的对象
     * @return desc
     */
    public static <T, V> List<V> convert(List<T> sourceList, Class<V> desc) {
        if (sourceList == null) {
            return null;
        }
        if (sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<V> resultList = new ArrayList<>();
        for (T source : sourceList) {
            V target = convert(source, desc);
            if (target != null) {
                resultList.add(target);
            }
        }
        return resultList;
    }
}