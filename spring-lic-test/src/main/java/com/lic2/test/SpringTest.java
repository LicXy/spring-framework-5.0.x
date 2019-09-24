package com.lic2.test;

import com.lic2.dao.UserDao;
import com.lic2.service.UserService;
import com.lic2.util.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);
		UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("userDao");
		userDao.query("123");
		System.out.println("---------------------");
		UserService userService = (UserService) annotationConfigApplicationContext.getBean("userDao");
		userService.query("123");
	}
}
