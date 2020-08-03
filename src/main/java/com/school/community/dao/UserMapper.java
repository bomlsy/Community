package com.school.community.dao;

import com.school.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

//和@Repository都可以，用于标识这个Bean可以被容器装配
@Mapper
public interface UserMapper {
    //根据id查询用户
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);
    //增加用户，并返回行数
    int insertUser(User user);
    //修改用户的状态
    int updateStatus(int id, int status);
    //修改用户头像的url
    int updateHeader(int id, String headerUrl);
    //修改用户密码
    int updatePassword(int id, String password);


}
