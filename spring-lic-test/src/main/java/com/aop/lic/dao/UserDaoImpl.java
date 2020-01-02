package com.aop.lic.dao;

import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl implements UserDao {
	@Override
	public void query() {
		System.out.println("Dao层:模拟查询...");
	}
}
