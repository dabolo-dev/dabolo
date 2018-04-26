package com.smartions.dabolo.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface IUserService {
	public Map<String,Object> signUp(String passwd);
	public Map<String,Object> signIn(String userId,String password,HttpServletResponse response);
	public int updatePassword(String userId,String oldPassword,String newPassword);
	public Map<String,Object> wechatConnect(String openId,String unionId,HttpServletResponse response);
	public void signUpActivity(String userId,String activityId,boolean flag);
	public void signInActivity(String userId,String activityId);
}
