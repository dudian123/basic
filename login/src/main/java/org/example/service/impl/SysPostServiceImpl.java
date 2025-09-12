package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.entity.SysPost;
import org.example.domain.bo.SysPostBo;
import org.example.domain.vo.SysPostVo;
import org.example.mapper.SysPostMapper;
import org.example.service.ISysPostService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 岗位信息 服务层处理
 *
 * @author Lion Li
 */
@Service
public class SysPostServiceImpl implements ISysPostService {

    private final SysPostMapper baseMapper;

    public SysPostServiceImpl(SysPostMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPostVo> selectPostList(SysPostBo post) {
        QueryWrapper<SysPost> wrapper = new QueryWrapper<>();
        if (post != null) {
            if (StringUtils.isNotBlank(post.getPostCode())) {
                wrapper.like("post_code", post.getPostCode());
            }
            if (StringUtils.isNotBlank(post.getPostName())) {
                wrapper.like("post_name", post.getPostName());
            }
            if (StringUtils.isNotBlank(post.getStatus())) {
                wrapper.eq("status", post.getStatus());
            }
            if (post.getDeptId() != null) {
                wrapper.eq("dept_id", post.getDeptId());
            }
        }
        wrapper.orderByAsc("post_sort");
        
        List<SysPost> postList = baseMapper.selectList(wrapper);
        List<SysPostVo> postVoList = new ArrayList<>();
        for (SysPost sysPost : postList) {
            SysPostVo postVo = new SysPostVo();
            postVo.setPostId(sysPost.getPostId());
            postVo.setTenantId(sysPost.getTenantId());
            postVo.setDeptId(sysPost.getDeptId());
            postVo.setPostCode(sysPost.getPostCode());
            postVo.setPostCategory(sysPost.getPostCategory());
            postVo.setPostName(sysPost.getPostName());
            postVo.setPostSort(sysPost.getPostSort());
            postVo.setStatus(sysPost.getStatus());
            postVo.setRemark(sysPost.getRemark());
            if (sysPost.getCreateTime() != null) {
                postVo.setCreateTime(Timestamp.valueOf(sysPost.getCreateTime()));
            }
            postVoList.add(postVo);
        }
        return postVoList;
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Override
    public List<SysPostVo> selectPostAll() {
        List<SysPost> postList = baseMapper.selectPostAll();
        List<SysPostVo> postVoList = new ArrayList<>();
        for (SysPost sysPost : postList) {
            SysPostVo postVo = convertToVo(sysPost);
            postVoList.add(postVo);
        }
        return postVoList;
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 岗位对象信息
     */
    @Override
    public SysPostVo selectPostById(Long postId) {
        SysPost post = baseMapper.selectPostById(postId);
        if (post == null) {
            return null;
        }
        return convertToVo(post);
    }

    /**
     * 根据用户ID查询岗位
     *
     * @param userId 用户ID
     * @return 岗位列表
     */
    @Override
    public List<SysPostVo> selectPostsByUserId(Long userId) {
        List<SysPost> postList = baseMapper.selectPostsByUserId(userId);
        List<SysPostVo> postVoList = new ArrayList<>();
        for (SysPost sysPost : postList) {
            SysPostVo postVo = convertToVo(sysPost);
            postVoList.add(postVo);
        }
        return postVoList;
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPostBo post) {
        Long postId = post.getPostId() == null ? -1L : post.getPostId();
        int count = baseMapper.checkPostNameUnique(post.getPostName(), postId);
        return count == 0;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostCodeUnique(SysPostBo post) {
        Long postId = post.getPostId() == null ? -1L : post.getPostId();
        int count = baseMapper.checkPostCodeUnique(post.getPostCode(), postId);
        return count == 0;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public long countUserPostByPostId(Long postId) {
        return baseMapper.countUserPostByPostId(postId);
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId) {
        return baseMapper.deleteById(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds) {
        int result = 0;
        for (Long postId : postIds) {
            result += baseMapper.deleteById(postId);
        }
        return result;
    }

    /**
     * 新增保存岗位信息
     *
     * @param bo 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPostBo bo) {
        SysPost post = new SysPost();
        BeanUtils.copyProperties(bo, post);
        return baseMapper.insert(post);
    }

    /**
     * 修改保存岗位信息
     *
     * @param bo 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPostBo bo) {
        SysPost post = new SysPost();
        BeanUtils.copyProperties(bo, post);
        return baseMapper.updateById(post);
    }

    /**
     * 转换为VO对象
     */
    private SysPostVo convertToVo(SysPost entity) {
        if (entity == null) {
            return null;
        }
        SysPostVo vo = new SysPostVo();
        vo.setPostId(entity.getPostId());
        vo.setTenantId(entity.getTenantId());
        vo.setDeptId(entity.getDeptId());
        vo.setPostCode(entity.getPostCode());
        vo.setPostCategory(entity.getPostCategory());
        vo.setPostName(entity.getPostName());
        vo.setPostSort(entity.getPostSort());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(Timestamp.valueOf(entity.getCreateTime()));
        }
        return vo;
    }
}