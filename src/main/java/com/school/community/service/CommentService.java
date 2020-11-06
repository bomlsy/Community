package com.school.community.service;

import com.school.community.dao.CommentMapper;
import com.school.community.dao.DiscussPostMapper;
import com.school.community.entity.Comment;
import com.school.community.util.CommunityConstant;
import com.school.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    DiscussPostService discussPostService;

    //查询每一页的评论
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    //查询评论条数
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    //添加评论
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//过滤评论中的HTML标签
        comment.setContent(sensitiveFilter.filter(comment.getContent()));//过滤敏感词
        int rows = commentMapper.insertComment(comment);

        //更新帖子的评论数量：只有评论帖子的时候才需要更新这个数量，因此要先做一个判断
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }
}
