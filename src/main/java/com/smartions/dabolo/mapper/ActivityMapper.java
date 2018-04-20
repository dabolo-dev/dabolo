package com.smartions.dabolo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ActivityMapper {
	int saveActivity(Map<String, Object> activity);

	int saveType(List<Map<String, Object>> activityAndType);

	int saveLabel(List<Map<String, Object>> activityAndLabel);

	int savePic(List<Map<String, Object>> activityAndPicList);

	Map<String, Object> getActivityInfo(@Param("activityId")String activityId);

	List<Map<String, Object>> getActivityType(@Param("activityId")String activityId);

	List<Map<String, Object>> getActivityLabel(@Param("activityId")String activityId);

	List<Map<String, Object>> getActivityPic(@Param("activityId")String activityId);
	
	List<Map<String,Object>> getLabelList();
	List<Map<String,Object>> getTypeList();
}
