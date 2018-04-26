package com.smartions.dabolo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.ActivityMapper;
import com.smartions.dabolo.model.Activity;

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
				List<Map<String, Object>> mapTypeList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> type : typelist) {
					if (activityId.equals(type.get("activity_and_type_activity_id"))) {
						mapTypeList.add(type);
					}
				}
				List<Map<String, Object>> mapLabelList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> label : labellist) {
					if (activityId.equals(label.get("activity_and_lable_activity_id"))) {
						mapLabelList.add(label);
					}
				}
				List<Map<String, Object>> mapPicList = new ArrayList<Map<String, Object>>();
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
			activityMapper.saveActivity(activityMap);
			List<Map<String, Object>> labelList = (List<Map<String, Object>>) activityMap.get("labeList");
			if (labelList.size() > 0) {
				activityMapper.saveLabel(labelList);
			}
			List<Map<String, Object>> picList = (List<Map<String, Object>>) activityMap.get("picList");
			if (picList.size() > 0) {
				activityMapper.savePic(picList);
			}
			List<Map<String, Object>> typeList = (List<Map<String, Object>>) activityMap.get("typeList");
			if (typeList.size() > 0) {
				activityMapper.saveType(typeList);
			}

			inDataMap.put("flag", 1);
		} catch (Exception e) {
			inDataMap.put("flag", 0);
		} finally {
			return inDataMap;
		}

	}

	@Override
	@Transactional
	public int setCover(String picName, String activityId) {
		// TODO Auto-generated method stub
		return activityMapper.setCover(picName, activityId);
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

	@Override
	@Transactional
	public int deletePicture(String picName) {
		// TODO Auto-generated method stub
		return activityMapper.deletePic(picName);
	}

	@Override
	@Transactional
	public Map<String, Object> saveActivity(Map<String, Object> activityMap) {
		Map<String, Object> inDataMap = new HashMap<String, Object>();
		try { // 保存基本信息
			activityMapper.editActivity(activityMap);
			// 保存新增类型
			List<Map<String, Object>> typeList = (List<Map<String, Object>>) activityMap.get("typeNewList");
			if (typeList.size() > 0) {
				activityMapper.saveType(typeList);
			}
			// 删除类型
			List<Map<String, Object>> typeRemoveList = (List<Map<String, Object>>) activityMap.get("typeRemoveList");
			if (typeList.size() > 0) {
				activityMapper.removeType(typeRemoveList,activityMap.get(Activity.ID).toString());
			}

			// 保存新增标签
			List<Map<String, Object>> labelList = (List<Map<String, Object>>) activityMap.get("labeNewList");
			if (labelList.size() > 0) {
				activityMapper.saveLabel(labelList);
			}

			// 删除标签
			List<Map<String, Object>> labeRemovelList = (List<Map<String, Object>>) activityMap.get("labeRemoveList");
			if (labelList.size() > 0) {
				activityMapper.removeLabel(labeRemovelList,activityMap.get(Activity.ID).toString());
			}

			// 保存新增图片
			List<Map<String, Object>> picList = (List<Map<String, Object>>) activityMap.get("picNewList");
			if (picList.size() > 0) {
				activityMapper.savePic(picList);
			}
			inDataMap.put("flag", 1);
		} catch (Exception e) {
			inDataMap.put("flag", 0);
		} finally {
			return inDataMap;
		}
	}

}
