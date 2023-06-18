package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    // offset和limit是用于分页功能，直接在展示post功能里一步到位了。offset：起始行号 limit：每页的最大页数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @param用于给参数起别名；
    // 如果只有一个参数且在<if>中使用即动态查询，则必须起别名。
    int selectDiscussPostRows(@Param("userId") int userId);

    //
    int insertDiscussPost(DiscussPost discussPost);

    // 查看帖子的详细信息
    DiscussPost selectDiscussPostById(int id);


}
