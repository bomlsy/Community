package com.school.community;

import com.school.community.dao.DiscussPostMapper;
import com.school.community.dao.UserMapper;
import com.school.community.entity.DiscussPost;
import com.school.community.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/*
    Mapper(DAO)接口测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//在这个测试类中也启用CommunityApplication作为配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    //要测试UserMapper，所以把UserMapper注入进来
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("adc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
    }

    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"lsy123");
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://test.com");
        System.out.println(rows);
    }

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post:list){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostsRows(149);
        System.out.println(rows);
    }

    }
