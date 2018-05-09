package com.smartions.dabolo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.ActivityMapper;
import com.smartions.dabolo.model.Activity;
import com.smartions.dabolo.model.WechatMessage;

@Service
public class ActivityService implements IActivityService {
	@Autowired
	private ActivityMapper activityMapper;

	@Autowired
	IWechatService wechatService;

	@Value("${activity.notify}")
	private String notify;

	@Value("${wechat.message.templeate-update-id}")
	private String templeateUpdateId;
	@Value("${wechat.message.templeate-notify-id}")
	private String templeateNotifyId;
	@Value("${wechat.message.data}")
	private String message;

	private Timer timer =null;

	public static final long dateToStamp(String dateStr) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = simpleDateFormat.parse(dateStr);
		long ts = date.getTime();
		return ts;
	}

	public static final void setActivityStatus(Map<String, Object> map, long now) {
		try {
			long start = dateToStamp(map.get("activity_start").toString());
			long signupStart = dateToStamp(map.get("activity_sign_up_start").toString());
			long sinupEnd = dateToStamp(map.get("activity_sign_up_end").toString());
			if ("cancel".equals(map.get("activity_status")) || "finish".equals(map.get("activity_status"))
					|| "draft".equals(map.get("activity_status"))) {
				return;
			}
			// 在报名开始之前：发布中
			if (now < signupStart) {
				map.put("activity_status", "publish");
			}
			// 报名开始到报名结束：报名中
			if (now >= signupStart && now < sinupEnd) {
				map.put("activity_status", "signuping");
			}
			// 报名结束到活动开始：准备中
			if (now >= sinupEnd && now < start) {
				map.put("activity_status", "preparing");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public List<Map<String, Object>> getActivityList() {
		List<Map<String, Object>> activityList = activityMapper.getActivityList();
		List<String> activityIds = new ArrayList<String>();
		long now = System.currentTimeMillis();
		for (Map<String, Object> map : activityList) {
			System.out.println(String.valueOf(map.get("activity_id")) + "+:activityId in");
			activityIds.add(String.valueOf(map.get("activity_id")));
			// 设置活动状态
			setActivityStatus(map, now);
		}
		if (activityIds.size() > 0) {

			List<Map<String, Object>> typelist = activityMapper.getActivityType(activityIds);
			List<Map<String, Object>> labellist = activityMapper.getActivityLabel(activityIds);
			List<Map<String, Object>> piclist = activityMapper.getActivityPic(activityIds);
			for (Map<String, Object> map : activityList) {
				String activityId = String.valueOf(map.get("activity_id"));
				System.out.println(activityId + "+:activityId");
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
				List<Map<String, Object>> userAndActivityList = activityMapper.getParticipateList(activityId);
				int count = 0;
				for (Map<String, Object> uaa : userAndActivityList) {
					count += Integer.parseInt(uaa.get("activity_and_user_persion_count").toString());
				}
				map.put("participateCount", count);
			}
		}
		return activityList;
	}

	@Override
	public Map<String, Object> getActivityInfo(String activityId) {
		// TODO Auto-generated method stub
		Map<String, Object> activity = activityMapper.getActivityInfo(activityId);
		long now = System.currentTimeMillis();
		if (activity != null) {
			setActivityStatus(activity, now);
			List<String> activityIds = new ArrayList<String>();
			activityIds.add(activityId);
			activity.put("typelist", activityMapper.getActivityType(activityIds));
			activity.put("labellist", activityMapper.getActivityLabel(activityIds));
			activity.put("piclist", activityMapper.getActivityPic(activityIds));
			activity.put("participateList", activityMapper.getParticipateList(activityId));
			activity.put("attentionList", activityMapper.getAttentionList(activityId));
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
				activityMapper.removeType(typeRemoveList, activityMap.get(Activity.ID).toString());
			}

			// 保存新增标签
			List<Map<String, Object>> labelList = (List<Map<String, Object>>) activityMap.get("labeNewList");
			if (labelList.size() > 0) {
				activityMapper.saveLabel(labelList);
			}

			// 删除标签
			List<Map<String, Object>> labeRemovelList = (List<Map<String, Object>>) activityMap.get("labeRemoveList");
			if (labelList.size() > 0) {
				activityMapper.removeLabel(labeRemovelList, activityMap.get(Activity.ID).toString());
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

	@Override
	public Map<String, Object> getComments(String activityId, int currentPage, int pageSize) {
		Map<String, Object> inDataMap = new HashMap<String, Object>();
		// 获取评论条数
		Integer countMap = activityMapper.getCountComment(activityId);
		if (countMap == null)
			return inDataMap;
		int count = countMap.intValue();
		int pageCount = (count + pageSize - 1) / pageSize;
		inDataMap.put("count", count);
		inDataMap.put("pageCount", pageCount);
		// 获取评论
		int start = (currentPage - 1) * pageSize;
		List<Map<String, Object>> commentList = activityMapper.getCommentList(activityId, start, pageSize, "wechat");
		inDataMap.put("commentList", commentList);
		// 获取图片
		List<String> commentIds = new ArrayList<String>();
		for (Map<String, Object> comment : commentList) {
			commentIds.add(comment.get("comment_id").toString());
		}
		if (commentIds.size() > 0) {
			List<Map<String, Object>> picList = activityMapper.getCommentPicList(commentIds);
			for (Map<String, Object> comment : commentList) {
				List<Map<String, Object>> commentPicList = new ArrayList<Map<String, Object>>();
				comment.put("picList", commentPicList);
				for (Map<String, Object> pic : picList) {
					if (comment.get("comment_id").equals(pic.get("comment_and_pic_comment_id"))) {
						commentPicList.add(pic);
					}
				}
			}
		}

		return inDataMap;
	}

	@Override
	public void notifyPlanMessage() {
		if(timer!=null) {
			timer.cancel();
		}
		timer= new Timer();
		
		List<Map<String, Object>> toBeStartList = activityMapper.getToBeStart();

		long now = System.currentTimeMillis();
		for (Map<String, Object> activity : toBeStartList) {
			try {
				List<Map<String, Object>> participateList = activityMapper
						.getParticipateList(activity.get("activity_id").toString());
				List<String> userIdList = new ArrayList<String>();
				userIdList.add(activity.get("activity_creator").toString());
				for (Map<String, Object> participate : participateList) {
					userIdList.add(participate.get("activity_and_user_user_id").toString());
				}
				final List<Map<String, Object>> userInfoList = activityMapper.getOpenIdsByUserIdS(userIdList);
				long startTime = dateToStamp(activity.get("activity_start").toString()) - Long.parseLong(notify);

				long delayTime = startTime - now;
				if (delayTime > 0) {
					timer.schedule(new TimerTask() {
						public void run() {
							// 获取活动所有参与者的openid
							for (Map<String, Object> userInfo : userInfoList) {
								// 发送信息
								WechatMessage wm = new WechatMessage();
								wm.setTouser(userInfo.get("third_id").toString());
								Map<String,Object> data=new HashMap<String,Object>();
								data.put("keyword1", setPar("value",activity.get("activity_title")));
								data.put("keyword2", setPar("value",activity.get("activity_start")));
								data.put("keyword3", setPar("value",activity.get("activity_location")));
								data.put("keyword4", setPar("value",activity.get("activity_desc")));
								data.put("keyword5", setPar("value",message));
								wm.setData(data);
								wm.setFormId(userInfo.get("user_id").toString());
								wm.setTemplateId(templeateNotifyId);
								wechatService.sendMessage(wm);
							}

						}
					}, delayTime);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void sendMessage(String activityId,String updateMessage) {

		List<Map<String, Object>> participateList = activityMapper.getParticipateList(activityId);
		Map<String, Object> activity =activityMapper.getActivityInfo(activityId);
		final List<String> userIdList = new ArrayList<String>();
		for (Map<String, Object> participate : participateList) {
			userIdList.add(participate.get("activity_and_user_user_id").toString());
		}
		if(userIdList.size()>0) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<Map<String, Object>> userInfoList = activityMapper.getOpenIdsByUserIdS(userIdList);
					// 获取活动所有参与者的openid
					for (Map<String, Object> userInfo : userInfoList) {
						// 发送信息
						WechatMessage wm = new WechatMessage();
						wm.setTouser(userInfo.get("third_id").toString());
						Map<String,Object> data=new HashMap<String,Object>();
						data.put("keyword1", setPar("value",activity.get("activity_title")));
						data.put("keyword2", setPar("value",activity.get("activity_location")));
						data.put("keyword3", setPar("value",updateMessage));
						data.put("keyword4", setPar("value",activity.get("activity_desc")));
						wm.setData(data);
						wm.setFormId(userInfo.get("user_id").toString());
						wm.setTemplateId(templeateUpdateId);
						wechatService.sendMessage(wm);
					}
					
				}
			}).start();
		}
		

	}
	private Map<String,Object> setPar(String key,Object value){
		Map<String,Object> data=new HashMap<String,Object>();
		data.put(key, value);
		return data;
	}

}
