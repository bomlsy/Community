package com.school.community.service;

import com.school.community.dao.LoginTicketMapper;
import com.school.community.dao.UserMapper;
import com.school.community.entity.LoginTicket;
import com.school.community.entity.User;
import com.school.community.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //@Autowired
    //private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    //当要注入一个固定值而不是bean的时候，使用Value注解
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        //return userMapper.selectById(id);
        //从缓存中查询用户
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    public User findUserByUsername(String username) {
        return userMapper.selectByName(username);
    }

    //注册功能
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null){
            map.put("emailMsg", "该邮箱已存在！");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        //注册的时候生成激活码，并存入数据库中
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //设置此功能页面的路径 http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活邮件", content);

        return map;
    }

    //激活功能
    //传入用户id和激活码
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    //登录
    //传入用户名、明文的密码、多少秒后登录凭证失效
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值判断
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        //验证账号：是否存在，若存在，密码是否一致
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "您输入的账号不存在！");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg", "您输入的密码不正确！");
            return map;
        }
        //生成登录凭证，用于记录用户的登录状态（cookie）
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));//当前时间往后推移expiredSeconds * 1000秒
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicket(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket); //Redis会把loginTicket序列化成JSON格式的字符串并保存


        //只需要返回给浏览器ticket的值，在浏览器下一次访问服务器的时候，把ticket一并发送给服务器
        //服务器根据ticket的值，去表login_ticket表中查询user_id和status以及expired
        map.put("ticket", loginTicket.getTicket());
        return  map;
    }

    //登出
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicket(ticket);
        //先把ticket取出来，修改状态为1后，再存回去
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);


    }

    //查询登录用户的ticket
    public LoginTicket findLoginTicket(String ticket){
       // return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicket(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //更新用户表中的headerUrl
    public int updateHeader(int userId, String headerUrl){
       int rows = userMapper.updateHeader(userId, headerUrl);
       clearCache(userId);
       return rows;
    }

    //修改密码
    public Map<String, Object> updatePassword(String oldPassword, String newPassword){
        Map<String, Object> map = new HashMap<>();

        //空值判断
        if (StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg","请输入原始密码！");
            return map;
        }
        //检验原始密码
        //获取当前登录用户的信息
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword+user.getSalt());
        if (!oldPassword.equals(user.getPassword())){
            map.put("oldPasswordMsg", "您输入的原始密码有误！");
            return map;
        }
        //新密码：空值判断
        if (StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "密码不能为空！");
            return map;
        }
        //修改密码
        int userId = user.getId();
        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        userMapper.updatePassword(userId, newPassword);
        clearCache(userId);
        return map;
    }

    //缓存
    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUser(userId);
        return (User)redisTemplate.opsForValue().get(redisKey);
    }
    // 2.取不到时初始化缓存数据:从MySQL中取数据，存入Redis中
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUser(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    // 3.数据变更时，清楚缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUser(userId);
        redisTemplate.delete(redisKey);
    }
}
