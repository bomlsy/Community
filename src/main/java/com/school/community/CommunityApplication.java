package com.school.community;
/*
	配置类
 */
import com.school.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//程序的入口使用此注解标识
@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		//自动创建了Spring容器，容器会自动扫描某些包下的某些bean，并装配到容器中
		//扫描配置类所在的包以及子包下的bean
		//并且，类前需要有这四种注解 Controller Service Component Repository
		SpringApplication.run(CommunityApplication.class, args);
	}

}
