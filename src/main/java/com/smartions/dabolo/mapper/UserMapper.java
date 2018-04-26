package com.smartions.dabolo.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;



public interface UserMapper {
	public int signUp(Map<String,Object> user);
	public Map<String,Object> getUser(@Param("userId") String userId);
	public int updatePassword(Map<String,Object> user);
	public String getUserIdByWechatOpenId(@Param("openId") String openId);
	public void bindThirdAndUser(Map<String,Object> third);
	public Map<String,Object> getUserAndActivity(@Param("userId") String userId,@Param("activityId") String activityId);
	public int editUserAndActivity(Map<String,Object> userAndActivity);
	public int saveUserAndActivity(Map<String,Object> userAndActivity);
	
}
