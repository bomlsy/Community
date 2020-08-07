package com.school.community.controller.interceptor;

import com.school.community.entity.LoginTicket;
import com.school.community.entity.User;
import com.school.community.service.UserService;
import com.school.community.util.CookieUtil;
import com.school.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/*
    定义拦截器的类:用于拦截所有页面，并在判断登录状态后，显示登录信息
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    //controller开始之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中查询ticket
        String ticket = CookieUtil.getValue(request, "ticket");
        //ticket不为空，说明登录成功
        if (ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //查询凭证是否有效：status=0、createTime晚于当前时间
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //把本次请求中的用户数据保存起来
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    //在模板引擎被调用之前，把User的数据存到model中
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    //在模板引擎执行完后，清理数据
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.remove();
    }
}
