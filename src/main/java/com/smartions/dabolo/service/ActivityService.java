package com.smartions.dabolo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.ActivityMapper;

@Service
public class ActivityService implements IActivityService {
	@Autowired
	private ActivityMapper activityMapper;

	@Override
	public List<Map<String, Object>> getActivityList() {
		List<Map<String, Object>> activityList = activityMapper.getActivityList();
		List<String> activityIds = new ArrayList<String>();
		for (Map<String, Object> map : activityList) {
			activityIds.add(String.valueOf(map.get("activity_id")));
		}
		if (activityIds.size() > 0) {

			List<Map<String, Object>> typelist = activityMapper.getActivityType(activityIds);
			List<Map<String, Object>> labellist = activityMapper.getActivityLabel(activityIds);
			List<Map<String, Object>> piclist = activityMapper.getActivityPic(activityIds);
			for (Map<String, Object> map : activityList) {
				String activityId = String.valueOf(map.get("activity_id"));
				List<Map<String, Object>> mapTypeList=new ArrayList<Map<String,Object>>();
				for (Map<String, Object> type : typelist) {
					if (activityId.equals(type.get("activity_and_type_activity_id"))) {
						mapTypeList.add(type);
					}
				}
				List<Map<String, Object>> mapLabelList=new ArrayList<Map<String,Object>>();
				for (Map<String, Object> label : labellist) {
					if (activityId.equals(label.get("activity_and_lable_activity_id"))) {
						mapLabelList.add(label);
					}
				}
				List<Map<String, Object>> mapPicList=new ArrayList<Map<String,Object>>();
				for (Map<String, Object> pic : piclist) {
					if (activityId.equals(pic.get("pic_activity_id"))) {
						mapPicList.add(pic);
					}
				}
				map.put("typelist", mapTypeList);
				map.put("labellist", mapLabelList);
				map.put("piclist", mapPicList);
			}
		}
		return activityList;
	}

	@Override
	public Map<String, Object> getActivityInfo(String activityId) {
		// TODO Auto-generated method stub
		Map<String, Object> activity = activityMapper.getActivityInfo(activityId);
		if (activity != null) {
			List<String> activityIds = new ArrayList<String>();
			activityIds.add(activityId);
			activity.put("typelist", activityMapper.getActivityType(activityIds));
			activity.put("labellist", activityMapper.getActivityLabel(activityIds));
			activity.put("piclist", activityMapper.getActivityPic(activityIds));
		}
		return activity;
	}

	@Override
	@Transactional
	public Map<String, Object> createActivity(String userId, Map<String, Object> activityMap) {
		Map<String, Object> inDataMap = new HashMap<String, Object>();
		try {
			// 2.保存活动
			activityMapper.saveActivity(activityMap);

			// 3.保存标签
			activityMapper.saveLabel((List<Map<String, Object>>) activityMap.get("labeList"));
			// 4.保存图片
			// activityMapper.savePic( (List<Map<String, Object>>)
			// activityMap.get("picList"));
			activityMapper.saveType((List<Map<String, Object>>) activityMap.get("typeList"));
			inDataMap.put("flag", 1);
		} catch (Exception e) {
			inDataMap.put("flag", 0);
		} finally {
			return inDataMap;
		}

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
