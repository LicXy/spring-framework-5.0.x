package com.lic.util;


import com.lic.service.UserService;
import com.lic.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan("com.lic")
@Configuration
public class AppConfig {

	@Bean
	public UserService userService(){
		return new UserServiceImpl();
	}

}
