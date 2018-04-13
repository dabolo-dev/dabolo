package com.smartions.dabolo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.UserMapper;
import com.smartions.dabolo.model.User;

@Service
public class UserService implements IUserService {
	@Autowired
	private UserMapper userMapper;
	@Override
	@Transactional
	public List<User> getAllUser() {
		// TODO Auto-generated method stub
		return userMapper.findUserInfo();
	}
	
}
