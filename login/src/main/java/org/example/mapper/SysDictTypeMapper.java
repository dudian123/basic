package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.entity.SysDictType;

import java.util.List;

/**
 * 字典类型表 数据层
 *
 * @author ruoyi
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    SysDictType selectDictTypeByType(@Param("dictType") String dictType);

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    SysDictType selectDictTypeById(@Param("dictId") Long dictId);

    /**
     * 查询字典类型列表
     *
     * @param dictType 字典类型
     * @return 字典类型集合信息
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    int checkDictTypeUnique(@Param("dictType") String dictType);

    /**
     * 新增字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    int updateDictType(SysDictType dictType);

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典类型ID
     * @return 结果
     */
    int deleteDictTypeByIds(@Param("dictIds") Long[] dictIds);

    /**
     * 删除字典类型信息
     *
     * @param dictId 字典类型ID
     * @return 结果
     */
    int deleteDictTypeById(@Param("dictId") Long dictId);
}