package com.school.community.service;

import com.school.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*
    业务组件
 */

//使得这个Bean可以被容器管理
@Service
//使得这个Bean可以被实例化多次，默认参数为singleton,即只被实例化一次
//@Scope("prototype")
public class AlphaService {

    //注入AlphaDao
    @Autowired
    private AlphaDao alphaDao;

    //构造器
    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    //这个Bean的初始化方法
    //这个注解用于让容器帮助管理这个初始化方法，即可以随时调用这个方法
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    //这个Bean的销毁方法
    //这个注解用于容器在对象销毁之前调用这个方法
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    //模拟一个业务需求：查询
    public String find(){
        return alphaDao.select();
    }
}
