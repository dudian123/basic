package org.example.service;

import org.example.domain.entity.SysDictType;
import org.example.domain.vo.SysDictTypeVo;
import org.example.domain.bo.SysDictTypeBo;

import java.util.List;

/**
 * 字典类型 业务层
 *
 * @author ruoyi
 */
public interface ISysDictTypeService {

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    List<SysDictTypeVo> selectDictTypeList(SysDictTypeBo dictType);

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    List<SysDictTypeVo> selectDictTypeAll();

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictTypeVo> selectDictDataByType(String dictType);

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    SysDictTypeVo selectDictTypeById(Long dictId);

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    SysDictTypeVo selectDictTypeByType(String dictType);

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典类型ID
     */
    void deleteDictTypeByIds(Long[] dictIds);

    /**
     * 加载参数缓存数据
     */
    void loadingDictCache();

    /**
     * 清空字典缓存数据
     */
    void clearDictCache();

    /**
     * 重置字典缓存数据
     */
    void resetDictCache();

    /**
     * 新增保存字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    Boolean insertDictType(SysDictTypeBo bo);

    /**
     * 修改保存字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    Boolean updateDictType(SysDictTypeBo bo);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    boolean checkDictTypeUnique(SysDictTypeBo dictType);

    /**
     * 刷新字典缓存数据
     *
     * @param dictType 字典类型
     */
    void refreshCache(String dictType);
}