package com.smartions.dabolo.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.ActivityMapper;
import com.smartions.dabolo.utils.RSAUtils;
@Service
public class ActivityService implements IActivityService {
	@Autowired
	private ActivityMapper activityMapper;
	@Override
	public List<Map<String, Object>> getActivityList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getActivityInfo(String activityId) {
		// TODO Auto-generated method stub
		 Map<String, Object> activity=activityMapper.getActivityInfo(activityId);
		 if(activity!=null) {
			 activity.put("typelist", activityMapper.getActivityType(activityId));
			 activity.put("labellist", activityMapper.getActivityLabel(activityId));
			 activity.put("piclist", activityMapper.getActivityPic(activityId));
		 }
		return activity;
	}

	@Override
	@Transactional
	public Map<String, Object> createActivity(String userId, Map<String, Object> activityMap) {
		try {
			//1.生成活动id
			String activityId=RSAUtils.md5(userId+String.valueOf(System.currentTimeMillis()));
			//2.保存活动
			activityMapper.saveActivity(activityMap);
			
			//3.保存标签
			activityMapper.saveLabel((List<Map<String, Object>>) activityMap.get("labeList"));
			//4.保存图片
			activityMapper.savePic( (List<Map<String, Object>>) activityMap.get("picList"));
			activityMapper.saveType((List<Map<String, Object>>) activityMap.get("typeList"));
			return getActivityInfo(activityId);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<Map<String, Object>> getLabelList() {
		// TODO Auto-generated method stub
		return activityMapper.getLabelList();
	}

	@Override
	public List<Map<String, Object>> getTypeList() {
		// TODO Auto-generated method stub
		return activityMapper.getTypeList();
	}


}
