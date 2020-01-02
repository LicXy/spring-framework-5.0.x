package com.aop.lic.util;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProxyTest {

	@Pointcut("execution(* com.aop.lic.service.UserServiceImpl.query(..))")
	public void pointcut(){
		//该方法作用: 注解载体
	}

	@Before("pointcut()")
	public void before(){
		System.out.println("前置增强已执行");
	}

	@After("pointcut()")
	public void after(){
		System.out.println("后置通知已执行");
	}

}
