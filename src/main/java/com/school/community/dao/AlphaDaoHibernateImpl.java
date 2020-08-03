package com.school.community.dao;

import org.springframework.stereotype.Repository;

/*
    AlphaDao的实现类
 */
//为了使得Spring容器能装配此类，必须加上注解
//重命名Bean的名字为：alphaHibernate
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select(){
        return "hibernate";
    }
}
