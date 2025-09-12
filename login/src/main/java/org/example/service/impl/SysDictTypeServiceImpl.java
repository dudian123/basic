package org.example.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysDictTypeBo;
import org.example.domain.entity.SysDictData;
import org.example.domain.entity.SysDictType;
import org.example.domain.vo.SysDictDataVo;
import org.example.domain.vo.SysDictTypeVo;
import org.example.mapper.SysDictDataMapper;
import org.example.mapper.SysDictTypeMapper;
import org.example.service.DictService;
import org.example.service.ISysDictTypeService;
import org.example.utils.RedisUtils;
import org.example.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典类型 业务层处理
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService, DictService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;

    /**
     * 字典缓存前缀
     */
    private static final String DICT_CACHE_PREFIX = "dict:";

    @Override
    public List<SysDictTypeVo> selectDictTypeList(SysDictTypeBo dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(dictType.getDictName())) {
            wrapper.like(SysDictType::getDictName, dictType.getDictName());
        }
        if (StringUtils.isNotBlank(dictType.getDictType())) {
            wrapper.like(SysDictType::getDictType, dictType.getDictType());
        }
        if (StringUtils.isNotBlank(dictType.getStatus())) {
            wrapper.eq(SysDictType::getStatus, dictType.getStatus());
        }
        wrapper.orderByAsc(SysDictType::getDictId);
        
        List<SysDictType> list = dictTypeMapper.selectList(wrapper);
        return list.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public List<SysDictTypeVo> selectDictTypeAll() {
        List<SysDictType> list = dictTypeMapper.selectList(null);
        return list.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public List<SysDictTypeVo> selectDictDataByType(String dictType) {
        // 这个方法名可能有误，应该是查询字典类型，而不是字典数据
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        List<SysDictType> list = dictTypeMapper.selectList(wrapper);
        return list.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public SysDictTypeVo selectDictTypeById(Long dictId) {
        SysDictType dictType = dictTypeMapper.selectById(dictId);
        return convertToVo(dictType);
    }

    @Override
    public SysDictTypeVo selectDictTypeByType(String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        SysDictType entity = dictTypeMapper.selectOne(wrapper);
        return convertToVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = dictTypeMapper.selectById(dictId);
            if (ObjectUtil.isNotNull(dictType)) {
                // 删除字典类型对应的字典数据
                dictDataMapper.deleteDictDataByType(dictType.getDictType());
                // 删除字典类型
                dictTypeMapper.deleteById(dictId);
                // 清除缓存
                clearDictCache();
            }
        }
    }

    @Override
    public void loadingDictCache() {
        List<SysDictType> dictTypeList = dictTypeMapper.selectList(null);
        for (SysDictType dictType : dictTypeList) {
            String cacheKey = DICT_CACHE_PREFIX + dictType.getDictType();
            RedisUtils.setCacheObject(cacheKey, dictType, java.time.Duration.ofHours(24));
        }
    }

    @Override
    public void clearDictCache() {
        // 清空所有字典缓存
        RedisUtils.keys(DICT_CACHE_PREFIX + "*").forEach(key -> {
            RedisUtils.deleteObject(key);
        });
    }

    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertDictType(SysDictTypeBo bo) {
        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(bo, dictType);
        dictType.setCreateTime(LocalDateTime.now());
        
        int result = dictTypeMapper.insert(dictType);
        if (result > 0) {
            refreshCache(dictType.getDictType());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDictType(SysDictTypeBo bo) {
        SysDictType dictType = dictTypeMapper.selectById(bo.getDictId());
        if (ObjectUtil.isNull(dictType)) {
            return false;
        }
        
        String oldDictType = dictType.getDictType();
        BeanUtils.copyProperties(bo, dictType);
        dictType.setUpdateTime(LocalDateTime.now());
        
        int result = dictTypeMapper.updateById(dictType);
        if (result > 0) {
            // 如果字典类型发生变化，需要同步更新字典数据
            if (!oldDictType.equals(dictType.getDictType())) {
                dictDataMapper.updateDictDataType(oldDictType, dictType.getDictType());
            }
            refreshCache(dictType.getDictType());
            return true;
        }
        return false;
    }

    @Override
    public boolean checkDictTypeUnique(SysDictTypeBo dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType());
        if (ObjectUtil.isNotNull(dictType.getDictId())) {
            wrapper.ne(SysDictType::getDictId, dictType.getDictId());
        }
        return dictTypeMapper.selectCount(wrapper) == 0;
    }

    @Override
    public void refreshCache(String dictType) {
        String cacheKey = DICT_CACHE_PREFIX + dictType;
        RedisUtils.deleteObject(cacheKey);
        
        SysDictTypeVo dictTypeVo = selectDictTypeByType(dictType);
        if (ObjectUtil.isNotNull(dictTypeVo)) {
            RedisUtils.setCacheObject(cacheKey, dictTypeVo, java.time.Duration.ofHours(24));
        }
    }

    /**
     * 实体转换为VO
     */
    private SysDictTypeVo convertToVo(SysDictType entity) {
        if (ObjectUtil.isNull(entity)) {
            return null;
        }
        SysDictTypeVo vo = new SysDictTypeVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    // ==================== DictService接口实现 ====================

    @Override
    public String getDictLabel(String dictType, String dictValue, String separator) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return "";
        }
        
        List<SysDictDataVo> datas = selectDictDataByTypeInternal(dictType);
        if (ObjectUtil.isEmpty(datas)) {
            return "";
        }
        
        Map<String, String> map = datas.stream()
            .filter(data -> "0".equals(data.getStatus()))
            .collect(Collectors.toMap(
                SysDictDataVo::getDictValue, 
                SysDictDataVo::getDictLabel,
                (existing, replacement) -> existing
            ));
        
        if (StringUtils.contains(dictValue, separator)) {
            return Arrays.stream(dictValue.split(separator))
                .map(v -> map.getOrDefault(v.trim(), ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(separator));
        } else {
            return map.getOrDefault(dictValue, "");
        }
    }

    @Override
    public String getDictValue(String dictType, String dictLabel, String separator) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictLabel)) {
            return "";
        }
        
        List<SysDictDataVo> datas = selectDictDataByTypeInternal(dictType);
        if (ObjectUtil.isEmpty(datas)) {
            return "";
        }
        
        Map<String, String> map = datas.stream()
            .filter(data -> "0".equals(data.getStatus()))
            .collect(Collectors.toMap(
                SysDictDataVo::getDictLabel, 
                SysDictDataVo::getDictValue,
                (existing, replacement) -> existing
            ));
        
        if (StringUtils.contains(dictLabel, separator)) {
            return Arrays.stream(dictLabel.split(separator))
                .map(l -> map.getOrDefault(l.trim(), ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(separator));
        } else {
            return map.getOrDefault(dictLabel, "");
        }
    }

    @Override
    public Map<String, String> getAllDictByDictType(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return new LinkedHashMap<>();
        }
        
        List<SysDictDataVo> list = selectDictDataByTypeInternal(dictType);
        if (ObjectUtil.isEmpty(list)) {
            return new LinkedHashMap<>();
        }
        
        // 保证顺序
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (SysDictDataVo vo : list) {
            if ("0".equals(vo.getStatus())) {
                map.put(vo.getDictValue(), vo.getDictLabel());
            }
        }
        return map;
    }

    @Override
    public List<SysDictDataVo> getDictData(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return new ArrayList<>();
        }
        return selectDictDataByTypeInternal(dictType);
    }

    /**
     * 内部方法：根据字典类型查询字典数据
     */
    private List<SysDictDataVo> selectDictDataByTypeInternal(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getStatus, "0")
               .orderByAsc(SysDictData::getDictSort)
               .orderByAsc(SysDictData::getDictCode);
        
        List<SysDictData> list = dictDataMapper.selectList(wrapper);
        return list.stream().map(this::convertDictDataToVo).collect(Collectors.toList());
    }

    /**
     * 转换字典数据实体为VO
     */
    private SysDictDataVo convertDictDataToVo(SysDictData entity) {
        if (entity == null) {
            return null;
        }
        SysDictDataVo vo = new SysDictDataVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}