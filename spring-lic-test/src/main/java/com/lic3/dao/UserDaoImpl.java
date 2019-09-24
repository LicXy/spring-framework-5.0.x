package com.lic3.dao;

import org.springframework.stereotype.Component;

@Component("userDao")
public class UserDaoImpl implements  UserDao{
	public void query(){
		System.out.println("123");
	}
}
