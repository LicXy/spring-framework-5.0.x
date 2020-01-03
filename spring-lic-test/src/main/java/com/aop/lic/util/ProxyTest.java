package com.aop.lic.util;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 使用JDK动态代理的条件:
 *   1. 目标类有实现接口
 *   2. 目标类是接口
 *   3. 目标类是代理类
 *
 * 使用CGLIB代理的条件:
 *   1. 目标类没有有实现接口
 *   2. <aop:aspectj-autoproxy proxy-target-class="true" /> 强制使用CGLIB代理
 *   注意:设置 proxy-target-class="true" 之后就真的是CGLIB代理了吗?
 *       不一定, 如果目标类是接口或者是代理类, 则即使设置了proxy-target-class为true, 但是仍然会使用JDK动态代理
 */
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
