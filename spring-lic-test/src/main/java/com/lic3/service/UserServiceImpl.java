package com.lic3.service;

import com.lic3.dao.UserDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDao;

	@Override
	public void query(String key) {
		userDao.query();
	}
}
