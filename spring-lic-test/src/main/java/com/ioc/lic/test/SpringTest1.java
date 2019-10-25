package com.ioc.lic.test;

import com.ioc.lic.util.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringTest1 {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(AppConfig.class);

	}
}
