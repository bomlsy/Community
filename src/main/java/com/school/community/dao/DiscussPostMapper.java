package com.school.community.dao;

import com.school.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /*
        userId:为了之后开发个人主页时，查看我的帖子这个功能，因此虽然首页不显示user，还是需要保留这个参数
        offSet, limit：为了分页功能，offSet表示起始行，limit表示每页显示的最大行数
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offSet, int limit);

    //为了显示页码，需要查询一共有多少条数据
    //Param注解可以用于给参数取别名
    //如果方法只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostsRows(@Param("userId") int userId);

    //发布帖子：向表discuss_post中插入数据
    int insertDiscussPost(DiscussPost discussPost);

    //根据帖子id，查询出帖子详情
    DiscussPost selectDiscussPostById(int id);

    //添加评论后，修改帖子评论的数量
    int updateCommentCount(int id, int commentCount); //根据帖子id修改帖子评论的数量


}
