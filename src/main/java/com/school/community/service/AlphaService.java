package com.school.community.service;

import com.school.community.dao.AlphaDao;
import com.school.community.dao.DiscussPostMapper;
import com.school.community.dao.UserMapper;
import com.school.community.entity.DiscussPost;
import com.school.community.entity.User;
import com.school.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jws.soap.SOAPBinding;
import java.util.Date;

/*
    业务组件
 */

//使得这个Bean可以被容器管理
@Service
//使得这个Bean可以被实例化多次，默认参数为singleton,即只被实例化一次
//@Scope("prototype")
public class AlphaService {

    //注入AlphaDao
    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;//注入这个Bean 可以实现事务的隔离

    //构造器
    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    //这个Bean的初始化方法
    //这个注解用于让容器帮助管理这个初始化方法，即可以随时调用这个方法
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    //这个Bean的销毁方法
    //这个注解用于容器在对象销毁之前调用这个方法
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    //模拟一个业务需求：查询
    public String find(){
        return alphaDao.select();
    }

    //模拟一个业务需求：注册一个新用户并且自动发布一个帖子
    /*
        把这两个操作看成一个事务
     */
    //管理事务的方法1：声明式
    //加上注解即可
    /*
        Isolation: 指定隔离级别
        Propagation: 传播机制 - 业务方法A可能会调用业务方法B，而这两个业务方法都可能会加上注解Transactional去管理事务，那么以
        谁的事务机制为准
        REQUIRED：支持当前事务（A调用B，则A为当前事务），如果不存在则创建新事务
        REQUIRES_NEW：创建一个新事务，并且暂停当前事务
        NESTED：如果当前存在事务，则嵌套在该事务中执行（A调用B，B有独立的提交和回滚），否则和REQUIRED相同
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost discussPost = new DiscussPost();
        discussPost.setId(user.getId());
        discussPost.setTitle("Hello");
        discussPost.setContent("新人报道");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        //人为造一个错，看事务是否会回滚
        Integer.valueOf("abc"); //把abc这个字符串转为整数
        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //在这里实现我们想要的逻辑
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost discussPost = new DiscussPost();
                discussPost.setId(user.getId());
                discussPost.setTitle("你好");
                discussPost.setContent("我是新人");
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                //人为造一个错，看事务是否会回滚
                Integer.valueOf("abc"); //把abc这个字符串转为整数
                return "ok";
            }
        });
    }
}
