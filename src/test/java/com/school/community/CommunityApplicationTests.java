package com.school.community;

import com.school.community.config.AlphaConfig;
import com.school.community.dao.AlphaDao;
import com.school.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
//在这个测试类中也启用CommunityApplication作为配置类
@ContextConfiguration(classes = CommunityApplication.class)
//这个类要得到Spring容器，实现以下接口
class CommunityApplicationTests implements ApplicationContextAware {

	@Test
	void contextLoads() {
	}

	private ApplicationContext applicationContext;
	//重写接口中的方法
	//传入的参数ApplicationContext实际就是容器，且这个接口继承自HierarchicalBeanFactory，HB这个接口继承自BeanFactory
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		//当程序启动时，applicationContext被传进来，并且被我们做了记录
		this.applicationContext = applicationContext;
	}

	@Test
	//用于测试Spring容器获取Bean的功能
	public void testApplicationContext(){
		//测试是否可以通过容器来获取Bean
		System.out.println(applicationContext);//GenericWebApplicationContext@1f81aa00 实现类类名

		//从Spring容器中获取AlphaDao这个Bean
		//当AlphaDao出现两个实现类的时候，容器不知道该装配哪一个
		//此时，需要在要装配的类中加上注解 Primary
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		//使用Bean的名字，强制加载特定的Bean
		alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	//用于测试Spring容器管理Bean的初始化和销毁的方法的功能
	public void testBeanManager(){
		//获取AlphaService
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		/*
			alphaService = applicationContext.getBean(AlphaService.class);
			System.out.println(alphaService);
		 */
	}

	//@Test
	//用于测试Spring容器中装配一个第三方Bean
	/*
		public void testBeanConfig(){
			SimpleDateFormat simpleDateFormat =
					applicationContext.getBean(SimpleDateFormat.class);
			System.out.println(simpleDateFormat.format(new Date()));
		}
	*/

	//使用此注解来将AlphaDao注入给属性alphaDao
	@Autowired
	private AlphaDao alphaDao;
	@Autowired
	private AlphaService alphaService;
	@Autowired
	private SimpleDateFormat simpleDateFormat;
	//测试依赖注入
	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);

	}

}
