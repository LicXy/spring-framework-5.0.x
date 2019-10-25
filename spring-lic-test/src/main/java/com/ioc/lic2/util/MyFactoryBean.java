package com.ioc.lic2.util;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyFactoryBean implements FactoryBean,InvocationHandler {
	private Class clazz;

	public MyFactoryBean(Class clazz) {
		this.clazz=clazz;
	}

	@Override
	public Object getObject() throws Exception {
		Class[] clazzs = new Class[]{clazz};
		Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(),clazzs,this);
		return proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("JDK动态代理...");
		return proxy;
	}
}
