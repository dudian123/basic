package org.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysDeptBo;
import org.example.domain.entity.SysDept;
import org.example.domain.vo.SysDeptVo;
import org.example.mapper.SysDeptMapper;
import org.example.service.ISysDeptService;
import org.example.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptMapper baseMapper;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptVo> selectDeptList(SysDeptBo dept) {
        SysDept deptEntity = new SysDept();
        BeanUtils.copyProperties(dept, deptEntity);
        List<SysDept> depts = baseMapper.selectDeptList(deptEntity);
        return depts.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<SysDeptVo> selectDeptTreeList(SysDeptBo dept) {
        List<SysDeptVo> depts = selectDeptList(dept);
        return buildDeptTree(depts);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDeptVo> buildDeptTree(List<SysDeptVo> depts) {
        List<SysDeptVo> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDeptVo::getDeptId).collect(Collectors.toList());
        for (SysDeptVo dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<SysDeptVo> buildDeptTreeSelect(List<SysDeptVo> depts) {
        List<SysDeptVo> deptTrees = buildDeptTree(depts);
        return deptTrees;
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        return baseMapper.selectDeptListByRoleId(roleId, true);
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDeptVo selectDeptById(Long deptId) {
        SysDept dept = baseMapper.selectDeptById(deptId);
        if (ObjectUtil.isNull(dept)) {
            return null;
        }
        return convertToVo(dept);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return baseMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        int result = baseMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = baseMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDeptBo dept) {
        boolean exist = ObjectUtil.isNotNull(baseMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId(), dept.getTenantId()));
        if (exist) {
            return false;
        }
        return true;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        // 暂时不做数据权限校验
    }

    /**
     * 新增保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDeptBo bo) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(bo, dept);
        SysDept info = baseMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!"0".equals(info.getStatus())) {
            throw new RuntimeException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return baseMapper.insertDept(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDeptBo bo) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(bo, dept);
        SysDept newParentDept = baseMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = baseMapper.selectDeptById(dept.getDeptId());
        if (ObjectUtil.isNotNull(newParentDept) && ObjectUtil.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = baseMapper.updateDept(dept);
        if ("0".equals(dept.getStatus())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        Long[] deptIds = StringUtils.split(ancestors, ",", Long.class);
        baseMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = baseMapper.selectList(null);
        for (SysDept child : children) {
            if (StringUtils.isNotEmpty(child.getAncestors()) && child.getAncestors().contains("," + deptId + ",")) {
                child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
                baseMapper.updateDept(child);
            }
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId) {
        return baseMapper.deleteDeptById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDeptVo> list, SysDeptVo t) {
        // 得到子节点列表
        List<SysDeptVo> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDeptVo tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDeptVo> getChildList(List<SysDeptVo> list, SysDeptVo t) {
        List<SysDeptVo> tlist = new ArrayList<>();
        Iterator<SysDeptVo> it = list.iterator();
        while (it.hasNext()) {
            SysDeptVo n = it.next();
            if (ObjectUtil.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDeptVo> list, SysDeptVo t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 转换为VO对象
     */
    private SysDeptVo convertToVo(SysDept entity) {
        if (entity == null) {
            return null;
        }
        SysDeptVo vo = new SysDeptVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}