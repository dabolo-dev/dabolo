package com.smartions.dabolo.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface IUserService {
	public Map<String,Object> signUp(String passwd);
	public Map<String,Object> signIn(String userId,String password,HttpServletResponse response);
	public int updatePassword(String userId,String oldPassword,String newPassword);
	public Map<String,Object> wechatConnect(String openId,String unionId,String nickName,String avarUrl,HttpServletResponse response);
	public int signUpActivity(String userId,String activityId,boolean flag,int persionCount,String note);
	public void signInActivity(String userId,String activityId);
	public List<Map<String,Object>> organizationActivity(String userId);
	public List<Map<String,Object>> participateActivity(String userId);
	public List<Map<String,Object>> attentionActivity(String userId);
	public void commentActivity(Map<String, Object> result);
}
