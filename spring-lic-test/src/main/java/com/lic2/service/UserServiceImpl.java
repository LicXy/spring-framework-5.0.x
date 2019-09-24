package com.lic2.service;

import com.lic2.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements  UserService {

	@Resource
	private UserDao userDao;

	@Override
	public void query(String key) {
		userDao.query(key);
	}
}
