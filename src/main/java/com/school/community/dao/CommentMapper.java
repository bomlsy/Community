package com.school.community.dao;

import com.school.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //根据实体查询评论：查询帖子的评论or评论的评论
    //支持分页
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询评论的条数（用于分页）
    int selectCountByEntity(int entityType, int entityId);

    //添加评论:增加评论数据
    int insertComment(Comment comment);
}
