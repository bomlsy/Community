package com.school.community.service;

import com.oracle.tools.packager.mac.MacAppBundler;
import com.school.community.dao.LoginTicketMapper;
import com.school.community.dao.UserMapper;
import com.school.community.entity.LoginTicket;
import com.school.community.entity.User;
import com.school.community.util.CommunityConstant;
import com.school.community.util.CommunityUtil;
import com.school.community.util.HostHolder;
import com.school.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    //当要注入一个固定值而不是bean的时候，使用Value注解
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
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
        loginTicketMapper.insertLoginTicket(loginTicket);

        //只需要返回给浏览器ticket的值，在浏览器下一次访问服务器的时候，把ticket一并发送给服务器
        //服务器根据ticket的值，去表login_ticket表中查询user_id和status以及expired
        map.put("ticket", loginTicket.getTicket());
        return  map;
    }

    //登出
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);

    }

    //查询登录用户的ticket
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //更新用户表中的headerUrl
    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
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
        return map;
    }
}
