package com.smartions.dabolo.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;



public interface UserMapper {
	public int signUp(Map<String,Object> user);
	public Map<String,Object> getUser(@Param("userId") String userId);
}
