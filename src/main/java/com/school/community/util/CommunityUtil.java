package com.school.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    //封装JSON对象
    //code:编码 msg：提示信息 map：业务数据
    public static String getJSONString (int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            //遍历map对象的key
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }
    //可能没有业务数据
    public static String getJSONString (int code, String msg) {
        return getJSONString(code, msg, null);
    }
    //可能没有提示信息
    public static String getJSONString (int code) {
        return getJSONString(code, null, null);
    }

    //测试
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "lsy");
        map.put("age", "18");
        System.out.println(getJSONString(0, "ok", map));
    }
}
