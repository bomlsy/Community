package com.school.community.service;

import com.school.community.dao.DiscussPostMapper;
import com.school.community.entity.DiscussPost;
import com.school.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/*
    DiscussPostService的业务层
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offSet, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offSet, limit);
    }

    public int findDiscussRows(int userId){
        return discussPostMapper.selectDiscussPostsRows(userId);
    }

    //发布帖子
    public int addDiscussPost (DiscussPost discussPost) {
        //空值判断
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        //首先：将帖子或标题中的html标签转义(<script>这种)
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        //插入数据
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    //根据帖子id查询帖子
    public DiscussPost findDicussPostById (int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    //更新帖子的评论数量
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }


}
