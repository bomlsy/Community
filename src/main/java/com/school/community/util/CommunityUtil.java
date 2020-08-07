package com.school.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/*
    注册时使用的工具类
 */
public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    //MD5加密
    /*
        由于MD5加密相同字符串，每次都会生成相同的明文，因此，当用户设置的密码过于简单时，很容易被盗取
        所以需要 password+salt 后再用md5进行加密
     */
    public static String md5(String key){
        //字符串为空串或者null或者空格，都判定为空，此时不再使用md5进行加密
        if (StringUtils.isBlank(key)){
                return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
