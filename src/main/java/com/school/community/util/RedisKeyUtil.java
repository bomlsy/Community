package com.school.community.util;

public class RedisKeyUtil {

    private static final String SPILT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    //生成某个实体的赞
    //like:entity:entityType:entityId  -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPILT + entityType + SPILT + entityId;
    }

    //某个用户的赞
    //like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPILT + userId;
    }

    //某个用户关注的实体
    //followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPILT + userId + SPILT + entityType;
    }

    //某个用户拥有的粉丝数
    //follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int enityId) {
        return PREFIX_FOLLOWER + SPILT + entityType + SPILT + enityId;
    }

    //登录验证码
    //每个用户对应一个验证码，但是不能用userId来表示当前用户，因为用户需要输入验证码时还未登录，无法获取当前用户
    //发送一个凭证给用户，存到cookie里
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPILT + owner;
    }

    //登录的凭证
    public static String getTicket(String ticket) {
        return PREFIX_TICKET + SPILT + ticket;
    }

    //缓存
    public static String getUser(int userId) {
        return PREFIX_USER + SPILT + userId;
    }

}
