package com.ioc.lic.util;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan("com.ioc.lic")
@Configuration
public class AppConfig {

/*	@Bean
	public UserService userService2(){
		return new UserServiceImpl2();
	}

	@Bean
	public UserService userService(){
		userService2();
		return new UserServiceImpl();
	}*/

}
