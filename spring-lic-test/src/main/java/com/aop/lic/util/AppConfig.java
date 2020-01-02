package com.aop.lic.util;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@ComponentScan("com.aop")
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {
}
