package com.lic.service;

import com.lic.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/*@Service("userService2")*/
public class UserServiceImpl2 implements  UserService{
	@Autowired
	private UserDao userDao;

	public UserServiceImpl2() {
		System.out.println("UserServiceImpl2: 构造方法执行");
	}


	@PostConstruct
	public void init(){
		System.out.println("UserServiceImpl2: 自定义初始化方法执行");
	}

	@Override
	public void query() {
		userDao.query();
	}
}
