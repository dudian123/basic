package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysDictTypeBo;
import org.example.domain.vo.SysDictDataVo;
import org.example.domain.vo.SysDictTypeVo;
import org.example.service.DictService;
import org.example.service.ISysDictTypeService;
import org.example.utils.ExcelUtil;
import org.example.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 字典类型信息控制器
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    private final ISysDictTypeService dictTypeService;
    private final DictService dictService;

    /**
     * 查询字典类型列表
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    public R<List<SysDictTypeVo>> list(SysDictTypeBo dictType) {
        try {
            List<SysDictTypeVo> list = dictTypeService.selectDictTypeList(dictType);
            return R.ok(list);
        } catch (Exception e) {
            log.error("查询字典类型列表失败: {}", e.getMessage(), e);
            return R.fail("查询字典类型列表失败: " + e.getMessage());
        }
    }

    /**
     * 导出字典类型列表
     *
     * @param dictType 字典类型信息
     * @param response HTTP响应
     */
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    public void export(SysDictTypeBo dictType, HttpServletResponse response) {
        try {
            List<SysDictTypeVo> list = dictTypeService.selectDictTypeList(dictType);
            ExcelUtil.exportExcel(list, "字典类型", SysDictTypeVo.class, response);
            log.info("导出字典类型列表成功，共{}条数据", list.size());
        } catch (Exception e) {
            log.error("导出字典类型列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("导出字典类型列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询字典类型详细
     *
     * @param dictId 字典类型ID
     * @return 字典类型详细信息
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping("/{dictId}")
    public R<SysDictTypeVo> getInfo(@PathVariable Long dictId) {
        try {
            SysDictTypeVo dictType = dictTypeService.selectDictTypeById(dictId);
            return R.ok(dictType);
        } catch (Exception e) {
            log.error("查询字典类型详细失败: {}", e.getMessage(), e);
            return R.fail("查询字典类型详细失败: " + e.getMessage());
        }
    }

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型信息
     * @return 操作结果
     */
    @SaCheckPermission("system:dict:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictTypeBo dictType) {
        try {
            Boolean result = dictTypeService.insertDictType(dictType);
            if (result) {
                return R.ok("新增字典类型成功");
            } else {
                return R.fail("新增字典类型失败");
            }
        } catch (Exception e) {
            log.error("新增字典类型失败: {}", e.getMessage(), e);
            return R.fail("新增字典类型失败: " + e.getMessage());
        }
    }

    /**
     * 修改字典类型
     *
     * @param dictType 字典类型信息
     * @return 操作结果
     */
    @SaCheckPermission("system:dict:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictTypeBo dictType) {
        try {
            Boolean result = dictTypeService.updateDictType(dictType);
            if (result) {
                return R.ok("修改字典类型成功");
            } else {
                return R.fail("修改字典类型失败");
            }
        } catch (Exception e) {
            log.error("修改字典类型失败: {}", e.getMessage(), e);
            return R.fail("修改字典类型失败: " + e.getMessage());
        }
    }

    /**
     * 删除字典类型
     *
     * @param dictIds 字典类型ID数组
     * @return 操作结果
     */
    @SaCheckPermission("system:dict:remove")
    @DeleteMapping("/{dictIds}")
    public R<Void> remove(@PathVariable Long[] dictIds) {
        try {
            dictTypeService.deleteDictTypeByIds(dictIds);
            return R.ok("删除字典类型成功");
        } catch (Exception e) {
            log.error("删除字典类型失败: {}", e.getMessage(), e);
            return R.fail("删除字典类型失败: " + e.getMessage());
        }
    }

    /**
     * 刷新字典缓存
     *
     * @return 操作结果
     */
    @SaCheckPermission("system:dict:remove")
    @DeleteMapping("/refreshCache")
    public R<Void> refreshCache() {
        try {
            dictTypeService.resetDictCache();
            return R.ok("刷新字典缓存成功");
        } catch (Exception e) {
            log.error("刷新字典缓存失败: {}", e.getMessage(), e);
            return R.fail("刷新字典缓存失败: " + e.getMessage());
        }
    }

    /**
     * 获取字典选择框列表
     *
     * @return 字典类型集合
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping("/optionselect")
    public R<List<SysDictTypeVo>> optionselect() {
        try {
            List<SysDictTypeVo> dictTypes = dictTypeService.selectDictTypeAll();
            return R.ok(dictTypes);
        } catch (Exception e) {
            log.error("获取字典选择框列表失败: {}", e.getMessage(), e);
            return R.fail("获取字典选择框列表失败: " + e.getMessage());
        }
    }

    /**
     * 校验字典类型是否唯一
     *
     * @param dictType 字典类型
     * @return 校验结果
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping("/checkDictTypeUnique")
    public R<Boolean> checkDictTypeUnique(@RequestParam String dictType) {
        try {
            SysDictTypeBo bo = new SysDictTypeBo();
            bo.setDictType(dictType);
            Boolean unique = dictTypeService.checkDictTypeUnique(bo);
            return R.ok(unique);
        } catch (Exception e) {
            log.error("校验字典类型唯一性失败: {}", e.getMessage(), e);
            return R.fail("校验字典类型唯一性失败: " + e.getMessage());
        }
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    // 公开接口，无需权限验证
    @GetMapping("/getDictLabel")
    public R<String> getDictLabel(@RequestParam String dictType, @RequestParam String dictValue) {
        try {
            String label = dictService.getDictLabel(dictType, dictValue);
            return R.ok(label);
        } catch (Exception e) {
            log.error("获取字典标签失败: {}", e.getMessage(), e);
            return R.fail("获取字典标签失败: " + e.getMessage());
        }
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    // 公开接口，无需权限验证
    @GetMapping("/getDictValue")
    public R<String> getDictValue(@RequestParam String dictType, @RequestParam String dictLabel) {
        try {
            String value = dictService.getDictValue(dictType, dictLabel);
            return R.ok(value);
        } catch (Exception e) {
            log.error("获取字典值失败: {}", e.getMessage(), e);
            return R.fail("获取字典值失败: " + e.getMessage());
        }
    }

    /**
     * 根据字典类型获取字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    // 公开接口，无需权限验证
    @GetMapping("/getDictData/{dictType}")
    public R<List<SysDictDataVo>> getDictData(@PathVariable String dictType) {
        try {
            List<SysDictDataVo> dictData = dictService.getDictData(dictType);
            return R.ok(dictData);
        } catch (Exception e) {
            log.error("获取字典数据失败: {}", e.getMessage(), e);
            return R.fail("获取字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据字典类型获取所有字典值与标签的映射
     *
     * @param dictType 字典类型
     * @return 字典值与标签的映射
     */
    // 公开接口，无需权限验证
    @GetMapping("/getAllDict/{dictType}")
    public R<java.util.Map<String, String>> getAllDictByDictType(@PathVariable String dictType) {
        try {
            java.util.Map<String, String> dictMap = dictService.getAllDictByDictType(dictType);
            return R.ok(dictMap);
        } catch (Exception e) {
            log.error("获取字典映射失败: {}", e.getMessage(), e);
            return R.fail("获取字典映射失败: " + e.getMessage());
        }
    }
}