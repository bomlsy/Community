package com.school.community.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/*
    配置类
 */
//使用此注解表明这是一个配置类
@Configuration
public class AlphaConfig {
    //此注解用于将第三方的类变为可装配到Spring容器中的Bean
    //simpleDateFormat()这个方法返回的对象将被装配到Spring容器中，且这个Bean的名字就是方法名
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    }
}
