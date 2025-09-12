package org.example.controller.system;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.example.domain.bo.SysDeptBo;
import org.example.domain.vo.SysDeptVo;
import org.example.service.ISysDeptService;
import org.example.utils.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.utils.R;

/**
 * 部门信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    private final ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @GetMapping("/list")
    public R<List<SysDeptVo>> list(SysDeptBo dept) {
        try {
            List<SysDeptVo> depts = deptService.selectDeptList(dept);
            return R.ok(depts);
        } catch (Exception e) {
            return R.fail("查询部门列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询部门列表（排除节点）
     */
    @GetMapping("/list/exclude/{deptId}")
    public R<List<SysDeptVo>> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        try {
            List<SysDeptVo> depts = deptService.selectDeptList(new SysDeptBo());
            depts.removeIf(d -> d.getDeptId().intValue() == deptId || java.util.Arrays.asList(StringUtils.split(d.getAncestors(), ",")).contains(deptId + ""));
            return R.ok(depts);
        } catch (Exception e) {
            return R.fail("查询部门列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据部门编号获取详细信息
     */
    @GetMapping(value = "/{deptId}")
    public R<SysDeptVo> getInfo(@PathVariable Long deptId) {
        try {
            deptService.checkDeptDataScope(deptId);
            SysDeptVo dept = deptService.selectDeptById(deptId);
            return R.ok(dept);
        } catch (Exception e) {
            return R.fail("查询部门信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public R<List<SysDeptVo>> treeselect(SysDeptBo dept) {
        try {
            List<SysDeptVo> depts = deptService.selectDeptList(dept);
            return R.ok(deptService.buildDeptTreeSelect(depts));
        } catch (Exception e) {
            return R.fail("查询部门树失败: " + e.getMessage());
        }
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public R<Map<String, Object>> roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        try {
            List<SysDeptVo> depts = deptService.selectDeptList(new SysDeptBo());
            Map<String, Object> data = new HashMap<>();
            data.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
            data.put("depts", deptService.buildDeptTreeSelect(depts));
            return R.ok(data);
        } catch (Exception e) {
            return R.fail("查询角色部门树失败: " + e.getMessage());
        }
    }

    /**
     * 新增部门
     */
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDeptBo dept) {
        try {
            if (!deptService.checkDeptNameUnique(dept)) {
                return R.fail("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
            }
            int rows = deptService.insertDept(dept);
            return rows > 0 ? R.ok() : R.fail("新增失败");
        } catch (Exception e) {
            return R.fail("新增部门失败: " + e.getMessage());
        }
    }

    /**
     * 修改部门
     */
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDeptBo dept) {
        try {
            Long deptId = dept.getDeptId();
            deptService.checkDeptDataScope(deptId);
            if (!deptService.checkDeptNameUnique(dept)) {
                return R.fail("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
            } else if (dept.getParentId().equals(deptId)) {
                return R.fail("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
            } else if ("1".equals(dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0) {
                return R.fail("该部门包含未停用的子部门！");
            }
            int rows = deptService.updateDept(dept);
            return rows > 0 ? R.ok() : R.fail("修改失败");
        } catch (Exception e) {
            return R.fail("修改部门失败: " + e.getMessage());
        }
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        try {
            if (deptService.hasChildByDeptId(deptId)) {
                return R.warn("存在下级部门,不允许删除");
            }
            if (deptService.checkDeptExistUser(deptId)) {
                return R.warn("部门存在用户,不允许删除");
            }
            deptService.checkDeptDataScope(deptId);
            int rows = deptService.deleteDeptById(deptId);
            return rows > 0 ? R.ok() : R.fail("删除失败");
        } catch (Exception e) {
            return R.fail("删除部门失败: " + e.getMessage());
        }
    }
}