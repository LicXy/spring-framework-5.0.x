package com.aop.lic.service;

import com.aop.lic.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	public UserServiceImpl() {
		System.out.println("UserServiceImpl: 构造方法执行");
	}


	@PostConstruct
	public void init(){
		System.out.println("UserServiceImpl: 自定义初始化方法执行");
	}

	@Override
	public void query() {
		userDao.query();
	}
}
