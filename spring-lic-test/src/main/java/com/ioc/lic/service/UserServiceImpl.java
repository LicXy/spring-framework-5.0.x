package com.ioc.lic.service;

import com.ioc.lic.dao.UserDao;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service("userService")
public class UserServiceImpl implements UserService, DisposableBean {
	@Autowired
	private UserDao userDao;

	/*public UserServiceImpl() {
		System.out.println("UserServiceImpl: 构造方法执行");
	}
	@PostConstruct
	public void init(){
		System.out.println("UserServiceImpl: 自定义初始化方法执行");
	}*/

	@Override
	public void query() {
		userDao.query();
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("销毁方法调用了....");
	}

}
