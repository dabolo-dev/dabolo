package com.smartions.dabolo.service;

import java.util.List;
import java.util.Map;

public interface IActivityService {
	List<Map<String,Object>> getActivityList();

	Map<String,Object> getActivityInfo(String activityId);
	Map<String,Object> createActivity(String userId,Map<String,Object> activityMap);
	List<Map<String,Object>> getLabelList();
	List<Map<String,Object>> getTypeList();

}
