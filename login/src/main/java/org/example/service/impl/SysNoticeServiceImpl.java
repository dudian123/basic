package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.bo.SysNoticeBo;
import org.example.domain.entity.SysNotice;
import org.example.domain.vo.SysNoticeVo;
import org.example.mapper.SysNoticeMapper;
import org.example.service.ISysNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告 业务层处理
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {

    private final SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNoticeVo selectNoticeById(Long noticeId) {
        SysNotice notice = noticeMapper.selectNoticeById(noticeId);
        return convertToSysNoticeVo(notice);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNoticeVo> selectNoticeList(SysNoticeBo notice) {
        try {
            List<SysNotice> notices = noticeMapper.selectNoticeList();
            return notices.stream()
                    .map(this::convertToSysNoticeVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询公告列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询公告列表失败: " + e.getMessage());
        }
    }

    /**
     * 新增公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    @Override
    public Boolean insertNotice(SysNoticeBo bo) {
        SysNotice notice = BeanUtil.toBean(bo, SysNotice.class);
        notice.setCreateTime(LocalDateTime.now());
        return noticeMapper.insertNotice(notice) > 0;
    }

    /**
     * 修改公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    @Override
    public Boolean updateNotice(SysNoticeBo bo) {
        SysNotice notice = BeanUtil.toBean(bo, SysNotice.class);
        notice.setUpdateTime(LocalDateTime.now());
        return noticeMapper.updateNotice(notice) > 0;
    }

    /**
     * 删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public Boolean deleteNoticeByIds(Long[] noticeIds) {
        return noticeMapper.deleteNoticeByIds(noticeIds) > 0;
    }

    /**
     * 转换为SysNoticeVo
     */
    private SysNoticeVo convertToSysNoticeVo(SysNotice notice) {
        if (ObjectUtil.isNull(notice)) {
            return null;
        }
        SysNoticeVo vo = new SysNoticeVo();
        BeanUtils.copyProperties(notice, vo);
        return vo;
    }
}