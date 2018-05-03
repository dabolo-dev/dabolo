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

	List<Map<String, Object>> getActivityType(List<String> activityIds);

	List<Map<String, Object>> getActivityLabel(List<String> activityIds);

	List<Map<String, Object>> getActivityPic(List<String> activityIds);
	
	List<Map<String,Object>> getLabelList();
	List<Map<String,Object>> getTypeList();
	
	List<Map<String,Object>> getActivityList();
	
	int deletePic(@Param("picName")String picName);
	int setCover(@Param("picName")String picName, @Param("activityId")String activityId);
	
	
	
	int editActivity(Map<String, Object> activity);

	int removeType(List<Map<String, Object>> activityAndType,@Param("activityId")String activityId);

	int removeLabel(List<Map<String, Object>> activityAndLabel,@Param("activityId")String activityId);
	
	List<Map<String, Object>> getParticipateList(@Param("activityId")String activityId);
	int getCountComment(@Param("activityId")String activityId);
	List<Map<String, Object>> getCommentList(@Param("activityId")String activityId,@Param("start")int start,@Param("pageSize")int pageSize);
	List<Map<String, Object>> getCommentPicList(List<String> commentIds);


}
