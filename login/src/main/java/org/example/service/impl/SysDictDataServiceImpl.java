package org.example.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysDictDataBo;
import org.example.domain.entity.SysDictData;
import org.example.domain.vo.SysDictDataVo;
import org.example.domain.PageQuery;
import org.example.domain.TableDataInfo;
import org.example.mapper.SysDictDataMapper;
import org.example.service.ISysDictDataService;
import org.example.service.ISysDictTypeService;
import org.example.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典数据 业务层处理
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    private final SysDictDataMapper dictDataMapper;
    private final ISysDictTypeService dictTypeService;

    @Override
    public List<SysDictDataVo> selectDictDataList(SysDictDataBo dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = buildQueryWrapper(dictData);
        List<SysDictData> list = dictDataMapper.selectList(wrapper);
        return list.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public TableDataInfo<SysDictDataVo> selectDictDataList(SysDictDataBo dictData, PageQuery pageQuery) {
        LambdaQueryWrapper<SysDictData> wrapper = buildQueryWrapper(dictData);
        Page<SysDictData> page = pageQuery.build();
        IPage<SysDictData> dictDataPage = dictDataMapper.selectPage(page, wrapper);
        
        List<SysDictDataVo> dictDataVoList = dictDataPage.getRecords().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        return TableDataInfo.build(dictDataPage, dictDataVoList);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<SysDictData> buildQueryWrapper(SysDictDataBo dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(dictData.getDictType())) {
            wrapper.eq(SysDictData::getDictType, dictData.getDictType());
        }
        if (StringUtils.isNotBlank(dictData.getDictLabel())) {
            wrapper.like(SysDictData::getDictLabel, dictData.getDictLabel());
        }
        if (StringUtils.isNotBlank(dictData.getStatus())) {
            wrapper.eq(SysDictData::getStatus, dictData.getStatus());
        }
        wrapper.orderByAsc(SysDictData::getDictSort).orderByAsc(SysDictData::getDictCode);
        return wrapper;
    }

    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getDictValue, dictValue)
               .eq(SysDictData::getStatus, "0"); // 只查询正常状态的数据
        
        SysDictData dictData = dictDataMapper.selectOne(wrapper);
        return ObjectUtil.isNotNull(dictData) ? dictData.getDictLabel() : StringUtils.EMPTY;
    }

    @Override
    public SysDictDataVo selectDictDataById(Long dictCode) {
        SysDictData dictData = dictDataMapper.selectById(dictCode);
        return convertToVo(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dict", allEntries = true)
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData dictData = dictDataMapper.selectById(dictCode);
            if (ObjectUtil.isNotNull(dictData)) {
                dictDataMapper.deleteById(dictCode);
                // 刷新字典缓存
                dictTypeService.refreshCache(dictData.getDictType());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dict", key = "'dict_data:' + #bo.dictType")
    public Boolean insertDictData(SysDictDataBo bo) {
        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(bo, dictData);
        dictData.setCreateTime(LocalDateTime.now());
        
        int result = dictDataMapper.insert(dictData);
        if (result > 0) {
            // 刷新字典缓存
            dictTypeService.refreshCache(dictData.getDictType());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dict", key = "'dict_data:' + #bo.dictType")
    public Boolean updateDictData(SysDictDataBo bo) {
        SysDictData dictData = dictDataMapper.selectById(bo.getDictCode());
        if (ObjectUtil.isNull(dictData)) {
            return false;
        }
        
        String oldDictType = dictData.getDictType();
        BeanUtils.copyProperties(bo, dictData);
        dictData.setUpdateTime(LocalDateTime.now());
        
        int result = dictDataMapper.updateById(dictData);
        if (result > 0) {
            // 刷新字典缓存
            dictTypeService.refreshCache(dictData.getDictType());
            // 如果字典类型发生变化，也要刷新旧的字典类型缓存
            if (!oldDictType.equals(dictData.getDictType())) {
                dictTypeService.refreshCache(oldDictType);
            }
            return true;
        }
        return false;
    }

    @Override
    @Cacheable(cacheNames = "dict", key = "'dict_data:' + #dictType")
    public List<SysDictDataVo> selectDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getStatus, "0") // 只查询正常状态的数据
               .orderByAsc(SysDictData::getDictSort);
        
        List<SysDictData> list = dictDataMapper.selectList(wrapper);
        return list.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dict", key = "'dict_data:' + #dictType")
    public void deleteDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        
        dictDataMapper.delete(wrapper);
        // 刷新字典缓存
        dictTypeService.refreshCache(dictType);
    }

    /**
     * 实体转换为VO
     */
    private SysDictDataVo convertToVo(SysDictData entity) {
        if (ObjectUtil.isNull(entity)) {
            return null;
        }
        SysDictDataVo vo = new SysDictDataVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}