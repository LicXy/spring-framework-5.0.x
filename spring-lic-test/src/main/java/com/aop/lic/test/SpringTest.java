package com.aop.lic.test;

import com.aop.lic.service.UserService;
import com.aop.lic.util.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringTest {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);
		/*UserService userService = annotationConfigApplicationContext.getBean(UserService.class);*/
		UserService userService = (UserService) annotationConfigApplicationContext.getBean("userService");
	/*	UserService userService2 = (UserService) annotationConfigApplicationContext.getBean("userService");*/
		userService.query();

	}
}
