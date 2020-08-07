package com.school.community.controller.interceptor;

import com.school.community.annotation.LoginRequired;
import com.school.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/*
    定义拦截器的类：用于检查登录状态
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    //在请求之前检查登录状态
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断拦截的是不是方法
        if (handler instanceof HandlerMethod) {
            //把object类型的handler转换成HandlerMethod类型的
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取拦截到的对象
            Method method = handlerMethod.getMethod();
            //取出@LoginRequired注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (loginRequired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");//未登录，重定向到登录页面
                return false;
            }

        }
        return true;
    }
}
