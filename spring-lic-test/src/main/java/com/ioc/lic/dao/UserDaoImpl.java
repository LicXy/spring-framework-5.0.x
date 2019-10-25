package com.ioc.lic.dao;

import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl implements  UserDao{
	@Override
	public void query() {
		System.out.println("模拟查询...");
	}
}
