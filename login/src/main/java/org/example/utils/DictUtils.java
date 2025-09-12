package org.example.utils;

import org.example.service.DictService;
import org.example.domain.vo.SysDictDataVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 字典工具类
 * 提供便捷的字典翻译静态方法
 *
 * @author example
 */
@Component
public class DictUtils {

    private static DictService dictService;

    /**
     * 注入字典服务
     */
    public DictUtils(DictService dictService) {
        DictUtils.dictService = dictService;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue) {
        if (dictService == null) {
            return "";
        }
        return dictService.getDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue, String separator) {
        if (dictService == null) {
            return "";
        }
        return dictService.getDictLabel(dictType, dictValue, separator);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel) {
        if (dictService == null) {
            return "";
        }
        return dictService.getDictValue(dictType, dictLabel);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel, String separator) {
        if (dictService == null) {
            return "";
        }
        return dictService.getDictValue(dictType, dictLabel, separator);
    }

    /**
     * 获取字典下所有的字典值与标签
     *
     * @param dictType 字典类型
     * @return dictValue为key，dictLabel为值组成的Map
     */
    public static Map<String, String> getAllDictByDictType(String dictType) {
        if (dictService == null) {
            return java.util.Collections.emptyMap();
        }
        return dictService.getAllDictByDictType(dictType);
    }

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public static List<SysDictDataVo> getDictData(String dictType) {
        if (dictService == null) {
            return java.util.Collections.emptyList();
        }
        return dictService.getDictData(dictType);
    }

    /**
     * 检查字典值是否存在
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 是否存在
     */
    public static boolean isDictValueExist(String dictType, String dictValue) {
        String label = getDictLabel(dictType, dictValue);
        return StringUtils.isNotBlank(label);
    }

    /**
     * 检查字典标签是否存在
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 是否存在
     */
    public static boolean isDictLabelExist(String dictType, String dictLabel) {
        String value = getDictValue(dictType, dictLabel);
        return StringUtils.isNotBlank(value);
    }
}