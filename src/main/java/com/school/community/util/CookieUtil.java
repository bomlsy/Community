package com.school.community.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/*
    此工具类用于从Cookie中获取ticket：
    LoginTicketnterceptor中会使用
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request, String name){
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        //从request中获取到的是全部的cookie，要找到需要的cookie，要遍历这个数组
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
