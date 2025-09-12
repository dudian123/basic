package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysDictDataBo;
import org.example.domain.vo.SysDictDataVo;
import org.example.domain.PageQuery;
import org.example.domain.TableDataInfo;
import org.example.service.ISysDictDataService;
import org.example.utils.ExcelUtil;
import org.example.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 字典数据信息控制器
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    private final ISysDictDataService dictDataService;

    /**
     * 查询字典数据列表
     *
     * @param dictData 字典数据信息
     * @param pageQuery 分页查询参数
     * @return 字典数据集合
     */
    @SaCheckPermission("system:dictData:list")
    @GetMapping("/list")
    public TableDataInfo<SysDictDataVo> list(SysDictDataBo dictData, PageQuery pageQuery) {
        try {
            return dictDataService.selectDictDataList(dictData, pageQuery);
        } catch (Exception e) {
            log.error("查询字典数据列表失败: {}", e.getMessage(), e);
            return TableDataInfo.build();
        }
    }

    /**
     * 查询字典数据详细
     *
     * @param dictCode 字典数据ID
     * @return 字典数据详细信息
     */
    @SaCheckPermission("system:dictData:query")
    @GetMapping("/{dictCode}")
    public R<SysDictDataVo> getInfo(@PathVariable Long dictCode) {
        try {
            SysDictDataVo dictData = dictDataService.selectDictDataById(dictCode);
            return R.ok(dictData);
        } catch (Exception e) {
            log.error("查询字典数据详细失败: {}", e.getMessage(), e);
            return R.fail("查询字典数据详细失败: " + e.getMessage());
        }
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/type/{dictType}")
    public R<List<SysDictDataVo>> dictType(@PathVariable String dictType) {
        try {
            List<SysDictDataVo> data = dictDataService.selectDictDataByType(dictType);
            return R.ok(data);
        } catch (Exception e) {
            log.error("根据字典类型查询字典数据失败: {}", e.getMessage(), e);
            return R.fail("根据字典类型查询字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 导出字典数据列表
     *
     * @param dictData 字典数据信息
     * @param response HTTP响应
     */
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    public void export(SysDictDataBo dictData, HttpServletResponse response) {
        try {
            List<SysDictDataVo> list = dictDataService.selectDictDataList(dictData);
            ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo.class, response);
            log.info("导出字典数据列表成功，共{}条数据", list.size());
        } catch (Exception e) {
            log.error("导出字典数据列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("导出字典数据列表失败: " + e.getMessage());
        }
    }

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据信息
     * @return 操作结果
     */
    @SaCheckPermission("system:dictData:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictDataBo dictData) {
        try {
            Boolean result = dictDataService.insertDictData(dictData);
            if (result) {
                return R.ok("新增字典数据成功");
            } else {
                return R.fail("新增字典数据失败");
            }
        } catch (Exception e) {
            log.error("新增字典数据失败: {}", e.getMessage(), e);
            return R.fail("新增字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据信息
     * @return 操作结果
     */
    @SaCheckPermission("system:dictData:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictDataBo dictData) {
        try {
            Boolean result = dictDataService.updateDictData(dictData);
            if (result) {
                return R.ok("修改字典数据成功");
            } else {
                return R.fail("修改字典数据失败");
            }
        } catch (Exception e) {
            log.error("修改字典数据失败: {}", e.getMessage(), e);
            return R.fail("修改字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 删除字典数据
     *
     * @param dictCodes 字典数据ID数组
     * @return 操作结果
     */
    @SaCheckPermission("system:dictData:remove")
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        try {
            dictDataService.deleteDictDataByIds(dictCodes);
            return R.ok("删除字典数据成功");
        } catch (Exception e) {
            log.error("删除字典数据失败: {}", e.getMessage(), e);
            return R.fail("删除字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    // 公开接口，无需权限验证
    @GetMapping("/label")
    public R<String> getDictLabel(@RequestParam String dictType, @RequestParam String dictValue) {
        try {
            String dictLabel = dictDataService.selectDictLabel(dictType, dictValue);
            return R.ok(dictLabel);
        } catch (Exception e) {
            log.error("查询字典标签失败: {}", e.getMessage(), e);
            return R.fail("查询字典标签失败: " + e.getMessage());
        }
    }
}