package com.ioc.lic.test;

import com.ioc.lic.service.UserService;
import com.ioc.lic.util.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringTest {

	public static void main(String[] args) {
		/**
		 * 1. 应用上下文初始化方式(一: Java配置类方式)
		 */
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);
		UserService userService = annotationConfigApplicationContext.getBean(UserService.class);
		/*annotationConfigApplicationContext.getBean("");*/
		userService.query();

		/**
		 * 1. 应用上下文初始化方式(二: 配置文件方式)
		 */
		/*AnnotationConfigApplicationContext annotationConfigApplicationContext11 =
				new AnnotationConfigApplicationContext("springConf.xml");
		UserService userService11 = (UserService) annotationConfigApplicationContext11.getBean("");
		userService11.query();*/

		/**
		 * 2. 应用上下文初始化方式(三)
		 */
		/*ApplicationContext applicationContext1 = new ClassPathXmlApplicationContext("springConf.xml");
		UserService userService1 = (UserService) applicationContext1.getBean("xxx");
		userService1.query();*/

		/**
		 * 3(已过时). 应用上下文初始化方式(四)
		 */
		/*XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("bean.xml"));
		UserService userService2 = (UserService) bf.getBean("xxx");
		userService2.query();*/

	}
}
