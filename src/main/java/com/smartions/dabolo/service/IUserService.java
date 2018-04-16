package com.smartions.dabolo.service;

import java.util.Map;

public interface IUserService {
	public Map<String,Object> signUp(String passwd);
	public Map<String,Object> signIn(String userId,String password);
}
