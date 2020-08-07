package com.school.community.util;

import com.school.community.entity.User;
import org.springframework.stereotype.Component;

/*
    持有用户信息，用于代替session对象（考虑到多线程的问题）
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void remove(){
        users.remove();
    }

}
