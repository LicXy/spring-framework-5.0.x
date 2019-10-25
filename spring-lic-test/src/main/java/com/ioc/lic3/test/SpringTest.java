package com.ioc.lic3.test;

import com.ioc.lic3.dao.UserDao;
import com.ioc.lic3.util.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);

		UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("userDao");
		userDao.query();
	//	ApplicationContext context = new ClassPathXmlApplicationContext("beanFactoryTest.xml");


	}
}
