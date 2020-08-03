package com.school.community.service;

import com.school.community.dao.DiscussPostMapper;
import com.school.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    DiscussPostService的业务层
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offSet, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offSet, limit);
    }

    public int findDiscussRows(int userId){
        return discussPostMapper.selectDiscussPostsRows(userId);
    }


}
