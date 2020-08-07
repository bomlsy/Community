package com.school.community.config;

import com.school.community.controller.interceptor.AlphaInterceptor;
import com.school.community.controller.interceptor.LoginRequiredInterceptor;
import com.school.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
    拦截器的配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    //实现WebMvcConfigurer中的方法addInterceptors
    public void addInterceptors(InterceptorRegistry registry) {
        //添加一个拦截器：AlphaInterceptor
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")//排除掉静态资源的路径不拦截
                .addPathPatterns("/login", "/register");

        //添加一个拦截器：LoginTicketInterceptor
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");//排除掉静态资源的路径不拦截

        //添加一个拦截器：LoginRequiredInterceptor
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");//排除掉静态资源的路径不拦截
    }
}
