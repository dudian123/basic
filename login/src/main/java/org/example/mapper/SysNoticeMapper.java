package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.domain.entity.SysNotice;

import java.util.List;

/**
 * 通知公告表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Select("SELECT notice_id, tenant_id, notice_title, notice_type, notice_content, status, create_dept, create_by, create_time, update_by, update_time, remark FROM sys_notice WHERE notice_id = #{noticeId}")
    SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     *
     * @return 公告集合
     */
    @Select("SELECT notice_id, tenant_id, notice_title, notice_type, notice_content, status, create_dept, create_by, create_time, update_by, update_time, remark FROM sys_notice ORDER BY create_time DESC")
    List<SysNotice> selectNoticeList();

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    int insertNotice(SysNotice notice);

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    int updateNotice(SysNotice notice);

    /**
     * 删除公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] noticeIds);
}