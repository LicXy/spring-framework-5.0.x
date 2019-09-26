package com.lic.test;

import com.lic.service.UserService;
import com.lic.util.AppConfig;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class SpringTest {

	public static void main(String[] args) {
		/**
		 * 1. 应用上下文初始化方式(一)
		 */
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);
		UserService userService = annotationConfigApplicationContext.getBean(UserService.class);
		userService.query();

		/**
		 * 2. 应用上下文初始化方式(二)
		 */
		/*ApplicationContext applicationContext1 = new ClassPathXmlApplicationContext("bean.xml");
		UserService userService1 = (UserService) applicationContext1.getBean("xxx");
		userService1.query();*/

		/**
		 * 3(已过时). 应用上下文初始化方式(三)
		 */
		/*XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("bean.xml"));
		UserService userService2 = (UserService) bf.getBean("xxx");
		userService2.query();*/




	}
}
