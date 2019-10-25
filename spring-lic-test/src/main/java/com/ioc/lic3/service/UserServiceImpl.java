package com.ioc.lic3.service;

import com.ioc.lic3.dao.UserDao;

import javax.annotation.Resource;

public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDao;

	@Override
	public void query(String key) {
		userDao.query();
	}
}
