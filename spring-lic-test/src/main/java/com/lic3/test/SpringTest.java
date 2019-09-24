package com.lic3.test;

import com.lic3.dao.UserDao;
import com.lic3.util.AppConfig;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class SpringTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);

		UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("userDao");
		userDao.query();
	//	ApplicationContext context = new ClassPathXmlApplicationContext("beanFactoryTest.xml");


	}
}
