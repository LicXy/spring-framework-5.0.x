package com.ioc.lic.test;

import com.ioc.lic.service.UserService;
import com.ioc.lic.util.AppConfig;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class SpringTest {

	public static void main(String[] args) {
		/**
		 * 1. 应用上下文初始化方式(一: Java配置类方式)
		 */
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);
		UserService userService = (UserService) annotationConfigApplicationContext.getBean("userService");
		userService.query();
		/**
		 * 手动注册关闭钩子
		 */
		annotationConfigApplicationContext.registerShutdownHook();
		/**
		 * 1. 应用上下文初始化方式(二: 包路径配置方式)
		 */
	/*	AnnotationConfigApplicationContext annotationConfigApplicationContext11 =
				new AnnotationConfigApplicationContext("com.ioc.lic");
		UserService userService11 = (UserService) annotationConfigApplicationContext11.getBean("userService");
		userService11.query();*/
		/**
		 * 2. 应用上下文初始化方式(三)
		 */
	/*	ApplicationContext applicationContext1 = new ClassPathXmlApplicationContext("springConf.xml");
		UserService userService1 = (UserService) applicationContext1.getBean("userService");
		userService1.query();*/
		/**
		 * 3(已过时). 应用上下文初始化方式(四)
		 */
	/*	XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("springConf.xml"));
		UserService userService2 = (UserService) bf.getBean("userService");
		userService2.query();*/
	}
}
