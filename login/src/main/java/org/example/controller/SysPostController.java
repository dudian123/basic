package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysPostBo;
import org.example.domain.vo.SysPostVo;
import org.example.service.ISysPostService;
import org.example.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位信息
 *
 * @author Lion Li
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/post")
public class SysPostController {

    private final ISysPostService postService;

    public SysPostController(ISysPostService postService) {
        this.postService = postService;
    }

    /**
     * 获取岗位列表
     */
    @SaCheckPermission("system:post:list")
    @GetMapping("/list")
    public R<List<SysPostVo>> list(SysPostBo post) {
        try {
            List<SysPostVo> list = postService.selectPostList(post);
            return R.ok(list);
        } catch (Exception e) {
            log.error("获取岗位列表失败: {}", e.getMessage(), e);
            return R.fail("获取岗位列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @SaCheckPermission("system:post:query")
    @GetMapping(value = "/{postId}")
    public R<SysPostVo> getInfo(@PathVariable Long postId) {
        try {
            SysPostVo post = postService.selectPostById(postId);
            return R.ok(post);
        } catch (Exception e) {
            log.error("获取岗位详细信息失败: {}", e.getMessage(), e);
            return R.fail("获取岗位详细信息失败: " + e.getMessage());
        }
    }

    /**
     * 新增岗位
     */
    @SaCheckPermission("system:post:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysPostBo post) {
        try {
            if (!postService.checkPostNameUnique(post)) {
                return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
            } else if (!postService.checkPostCodeUnique(post)) {
                return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
            }
            int result = postService.insertPost(post);
            return result > 0 ? R.ok() : R.fail("操作失败");
        } catch (Exception e) {
            log.error("新增岗位失败: {}", e.getMessage(), e);
            return R.fail("新增岗位失败: " + e.getMessage());
        }
    }

    /**
     * 修改岗位
     */
    @SaCheckPermission("system:post:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysPostBo post) {
        try {
            if (!postService.checkPostNameUnique(post)) {
                return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
            } else if (!postService.checkPostCodeUnique(post)) {
                return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
            }
            int result = postService.updatePost(post);
            return result > 0 ? R.ok() : R.fail("操作失败");
        } catch (Exception e) {
            log.error("修改岗位失败: {}", e.getMessage(), e);
            return R.fail("修改岗位失败: " + e.getMessage());
        }
    }

    /**
     * 删除岗位
     */
    @SaCheckPermission("system:post:remove")
    @DeleteMapping("/{postIds}")
    public R<Void> remove(@PathVariable Long[] postIds) {
        try {
            for (Long postId : postIds) {
                if (postService.countUserPostByPostId(postId) > 0) {
                    SysPostVo post = postService.selectPostById(postId);
                    return R.fail("删除失败，岗位'" + post.getPostName() + "'已分配用户");
                }
            }
            int result = postService.deletePostByIds(postIds);
            return result > 0 ? R.ok() : R.fail("操作失败");
        } catch (Exception e) {
            log.error("删除岗位失败: {}", e.getMessage(), e);
            return R.fail("删除岗位失败: " + e.getMessage());
        }
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public R<List<SysPostVo>> optionselect() {
        try {
            List<SysPostVo> list = postService.selectPostAll();
            return R.ok(list);
        } catch (Exception e) {
            log.error("获取岗位选择框列表失败: {}", e.getMessage(), e);
            return R.fail("获取岗位选择框列表失败: " + e.getMessage());
        }
    }
}