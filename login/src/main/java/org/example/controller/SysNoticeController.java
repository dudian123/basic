package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysNoticeBo;
import org.example.domain.vo.SysNoticeVo;
import org.example.service.ISysNoticeService;
import org.example.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告 信息操作处理
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    private final ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @SaCheckPermission("system:notice:list")
    @GetMapping("/list")
    public R<List<SysNoticeVo>> list(SysNoticeBo notice) {
        try {
            List<SysNoticeVo> list = noticeService.selectNoticeList(notice);
            return R.ok(list);
        } catch (Exception e) {
            log.error("获取通知公告列表失败: {}", e.getMessage(), e);
            return R.fail("获取通知公告列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @SaCheckPermission("system:notice:query")
    @GetMapping(value = "/{noticeId}")
    public R<SysNoticeVo> getInfo(@PathVariable Long noticeId) {
        try {
            SysNoticeVo notice = noticeService.selectNoticeById(noticeId);
            return R.ok(notice);
        } catch (Exception e) {
            log.error("获取通知公告详细信息失败: {}", e.getMessage(), e);
            return R.fail("获取通知公告详细信息失败: " + e.getMessage());
        }
    }

    /**
     * 新增通知公告
     */
    @SaCheckPermission("system:notice:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysNoticeBo notice) {
        try {
            Boolean result = noticeService.insertNotice(notice);
            return result ? R.ok() : R.fail("新增失败");
        } catch (Exception e) {
            log.error("新增通知公告失败: {}", e.getMessage(), e);
            return R.fail("新增通知公告失败: " + e.getMessage());
        }
    }

    /**
     * 修改通知公告
     */
    @SaCheckPermission("system:notice:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysNoticeBo notice) {
        try {
            Boolean result = noticeService.updateNotice(notice);
            return result ? R.ok() : R.fail("修改失败");
        } catch (Exception e) {
            log.error("修改通知公告失败: {}", e.getMessage(), e);
            return R.fail("修改通知公告失败: " + e.getMessage());
        }
    }

    /**
     * 删除通知公告
     */
    @SaCheckPermission("system:notice:remove")
    @DeleteMapping("/{noticeIds}")
    public R<Void> remove(@PathVariable Long[] noticeIds) {
        try {
            Boolean result = noticeService.deleteNoticeByIds(noticeIds);
            return result ? R.ok() : R.fail("删除失败");
        } catch (Exception e) {
            log.error("删除通知公告失败: {}", e.getMessage(), e);
            return R.fail("删除通知公告失败: " + e.getMessage());
        }
    }
}