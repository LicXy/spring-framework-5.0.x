package com.lic.util;


import com.lic.service.UserService;
import com.lic.service.UserServiceImpl;
import com.lic.service.UserServiceImpl2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan("com.lic")
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
