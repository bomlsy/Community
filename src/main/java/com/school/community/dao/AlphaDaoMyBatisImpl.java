package com.school.community.dao;
/*
    假如想使用MyBatis技术替换Hibernate技术
 */

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaDaoMyBatisImpl implements AlphaDao {
    @Override
    public String select(){
        return "myBatis";
    }
}
