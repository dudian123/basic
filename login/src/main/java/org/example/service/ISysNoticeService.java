package org.example.service;

import org.example.domain.bo.SysNoticeBo;
import org.example.domain.vo.SysNoticeVo;

import java.util.List;

/**
 * 公告 业务层
 *
 * @author Lion Li
 */
public interface ISysNoticeService {

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    SysNoticeVo selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    List<SysNoticeVo> selectNoticeList(SysNoticeBo notice);

    /**
     * 新增公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    Boolean insertNotice(SysNoticeBo bo);

    /**
     * 修改公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    Boolean updateNotice(SysNoticeBo bo);

    /**
     * 删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    Boolean deleteNoticeByIds(Long[] noticeIds);
}