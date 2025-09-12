package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.domain.entity.SysPost;

import java.util.List;

/**
 * 岗位信息 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysPostMapper extends BaseMapper<SysPost> {

    /**
     * 查询岗位数据集合
     *
     * @param post 岗位信息
     * @return 岗位数据集合
     */
    @Select("SELECT * FROM sys_post ORDER BY post_sort")
    List<SysPost> selectPostList(SysPost post);

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Select("SELECT * FROM sys_post WHERE status = '0' ORDER BY post_sort")
    List<SysPost> selectPostAll();

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Select("SELECT * FROM sys_post WHERE post_id = #{postId}")
    SysPost selectPostById(@Param("postId") Long postId);

    /**
     * 根据用户ID查询岗位
     *
     * @param userId 用户ID
     * @return 岗位列表
     */
    @Select("SELECT p.* FROM sys_post p " +
            "LEFT JOIN sys_user_post up ON up.post_id = p.post_id " +
            "WHERE p.status = '0' AND up.user_id = #{userId}")
    List<SysPost> selectPostsByUserId(@Param("userId") Long userId);

    /**
     * 校验岗位名称是否唯一
     *
     * @param postName 岗位名称
     * @param postId   岗位ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_post WHERE post_name = #{postName} AND post_id != #{postId}")
    int checkPostNameUnique(@Param("postName") String postName, @Param("postId") Long postId);

    /**
     * 校验岗位编码是否唯一
     *
     * @param postCode 岗位编码
     * @param postId   岗位ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_post WHERE post_code = #{postCode} AND post_id != #{postId}")
    int checkPostCodeUnique(@Param("postCode") String postCode, @Param("postId") Long postId);

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_user_post WHERE post_id = #{postId}")
    int countUserPostByPostId(@Param("postId") Long postId);
}