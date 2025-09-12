package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.entity.SysDictData;

import java.util.List;

/**
 * 字典表 数据层
 *
 * @author ruoyi
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictData> selectDictDataByType(@Param("dictType") String dictType);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    String selectDictLabel(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    SysDictData selectDictDataById(@Param("dictCode") Long dictCode);

    /**
     * 查询字典数据
     *
     * @param dictData 字典数据
     * @return 字典数据集合信息
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新字典类型
     * @return 结果
     */
    int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);

    /**
     * 新增字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    int insertDictData(SysDictData dictData);

    /**
     * 修改字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    int updateDictData(SysDictData dictData);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    int deleteDictDataByIds(@Param("dictCodes") Long[] dictCodes);

    /**
     * 删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    int deleteDictDataById(@Param("dictCode") Long dictCode);

    /**
     * 根据字典类型删除字典数据
     *
     * @param dictType 字典类型
     * @return 结果
     */
    int deleteDictDataByType(@Param("dictType") String dictType);
}