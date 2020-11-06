package com.school.community.util;
/*
    常量的接口
 */
public interface CommunityConstant {
    //激活成功
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;
    //默认状态的登录凭证的保存时间
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12; //12 hours
    //勾选记住我的登录凭证的保存时间
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
    //实体类型：帖子
    int ENTITY_TYPE_POST = 1;
    //实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;
    //实体类型：用户
    int ENTITY_TYPE_USER = 3;
}
