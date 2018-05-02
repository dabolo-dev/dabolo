package com.smartions.dabolo.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.ActivityMapper;
import com.smartions.dabolo.mapper.UserMapper;
import com.smartions.dabolo.model.Third;
import com.smartions.dabolo.model.User;
import com.smartions.dabolo.utils.AES;
import com.smartions.dabolo.utils.RSAUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class UserService implements IUserService {
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private ActivityMapper activityMapper;

	@Autowired
	ITokenService tokenService;

	@Override
	@Transactional
	public Map<String, Object> signUp(String password) {

		// 1.生成秘钥对
		Map<String, String> mapKey = RSAUtils.createKeys();

		try {
			// 2.生成用户id
			String userId = RSAUtils.md5(mapKey.get(RSAUtils.PULIBC_KEY));
			String userPublic = mapKey.get(RSAUtils.PULIBC_KEY);
			// 3.使用密码加密私钥
			String privateEncode = AES.encrypt(mapKey.get(RSAUtils.PRIVATE_KEY).getBytes(), password);
			Map<String, Object> user = new HashMap<String, Object>();
			user.put(User.USER_ID, userId);
			user.put(User.PUBLIC_STR, userPublic);
			user.put(User.PRIVATE_STR, privateEncode);
			userMapper.signUp(user);
			user = userMapper.getUser(userId);
			user.remove(User.PRIVATE_STR);
			user.remove(User.ACTIVE);
			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> signIn(String userId, String password, HttpServletResponse response) {
		Map<String, Object> user = userMapper.getUser(userId);
		try {
			if (userId.equals(RSAUtils.md5(user.get(User.PUBLIC_STR).toString()))) {// 验证用户id与公钥匹配。
				String privateStr = AES.decrypt(user.get(User.PRIVATE_STR).toString(), password);
				if (RSAUtils.verify(password, user.get(User.PUBLIC_STR).toString(),
						RSAUtils.signStr(password, privateStr))) {// 验证公钥与解密的私钥是否匹配
					if (Boolean.parseBoolean(user.get(User.ACTIVE).toString())) {
						user.remove(User.PRIVATE_STR);
						user.remove(User.ACTIVE);
						String token = tokenService.createToken(privateStr, userId,
								user.get(User.PUBLIC_STR).toString());
						response.addHeader("token", token);
						return user;
					}

				}
			}

		} catch (Exception e) {

		}
		return new HashMap<String, Object>();
	}

	@Override
	@Transactional
	public int updatePassword(String userId, String oldPassword, String newPassword) {
		// TODO Auto-generated method stub
		Map<String, Object> user = userMapper.getUser(userId);
		try {
			if (userId.equals(RSAUtils.md5(user.get(User.PUBLIC_STR).toString()))) {// 验证用户id与公钥匹配。
				String privateStr = AES.decrypt(user.get(User.PRIVATE_STR).toString(), oldPassword);
				if (RSAUtils.verify(oldPassword, user.get(User.PUBLIC_STR).toString(),
						RSAUtils.signStr(oldPassword, privateStr))) {// 验证公钥与解密的私钥是否匹配
					if (Boolean.parseBoolean(user.get(User.ACTIVE).toString())) {
						String privateEncode = AES.encrypt(privateStr.getBytes(), newPassword);
						user.put(User.PRIVATE_STR, privateEncode);
						return userMapper.updatePassword(user);
					}

				}
			}

		} catch (Exception e) {

		}
		return 0;
	}

	@Override
	@Transactional
	public Map<String, Object> wechatConnect(String openId, String unionId,HttpServletResponse response) {
		// TODO Auto-generated method stub
		// 通过openid去查询是否用户存在
		String userId = userMapper.getUserIdByWechatOpenId(openId);
		if (userId == null) {
			// 不存在注册新用户,以unionId作为密码
			Map<String, Object> user = signUp(unionId);
			userId = user.get(User.USER_ID).toString();
			Map<String, Object> third = new HashMap<String, Object>();
			third.put(Third.USER_ID, userId);
			third.put(Third.THIRD_ID, openId);
			third.put(Third.THIRD_TYPE, "wechat");
			userMapper.bindThirdAndUser(third);
		}
		// 通过用户id和unionId登录

		return signIn(userId, unionId,response);
	}

	@Override
	@Transactional
	public int signUpActivity(String userId, String activityId,boolean flag,int persionCount,String note) {
		// TODO Auto-generated method stub
		//判断用户与活动是否存在关系
		Map<String, Object> map=userMapper.getUserAndActivity(userId, activityId);
		Map<String, Object> activity = activityMapper.getActivityInfo(activityId);
		if(activity==null) return -2;
		int limitCount=Integer.parseInt(activity.get("activity_allow_persion").toString());
		if(limitCount!=0) {
			if(flag) {
				
				int count = 0;
				List<Map<String, Object>> userAndActivityList = activityMapper.getParticipateList(activityId);
				for (Map<String, Object> uaa : userAndActivityList) {
					count += Integer.parseInt(uaa.get("activity_and_user_persion_count").toString());
				}
				if(count+persionCount>limitCount) {
					return -1;
				}
			}
			
		}
		Map<String, Object> userAndActivity=new HashMap<String,Object>();
		//判断报名人数限制
		if(map!=null) {//存在
			
			userAndActivity.put("userId", userId);
			userAndActivity.put("activityId", activityId);
			userAndActivity.put("activity_and_user_participate", flag);
			userAndActivity.put("activity_and_user_persion_count", persionCount);
			userAndActivity.put("activity_and_user_note", note);
			userMapper.editUserAndActivity(userAndActivity);
		}else {//不存在
			if(flag) {
				userAndActivity.put("activity_and_user_user_id", userId);
				userAndActivity.put("activity_and_user_activity_id", activityId);
				userAndActivity.put("activity_and_user_participate", flag);
				userAndActivity.put("activity_and_user_persion_count", persionCount);
				userAndActivity.put("activity_and_user_note", note);
				userMapper.saveUserAndActivity(userAndActivity);
			}
			
		}
		return 1;
		
	}

	@Override
	@Transactional
	public void signInActivity(String userId, String activityId) {
		// TODO Auto-generated method stub
		Map<String, Object> userAndActivity=new HashMap<String,Object>();
		userAndActivity.put("userId", userId);
		userAndActivity.put("activityId", activityId);
		userAndActivity.put("activity_and_user_signin", true);
		
		userMapper.editUserAndActivity(userAndActivity);
	}

	@Override
	public List<Map<String, Object>> organizationActivity(String userId) {
		Map<String, Object> userAndActivity=new HashMap<String,Object>();
		userAndActivity.put("userId", userId);
		List<Map<String, Object>> activityList = userMapper.organizationActivity(userAndActivity);
		List<String> activityIds = new ArrayList<String>();
		long now = System.currentTimeMillis();
		for (Map<String, Object> map : activityList) {
			System.out.println(String.valueOf(map.get("activity_id")) + "+:activityId in");
			activityIds.add(String.valueOf(map.get("activity_id")));
			// 设置活动状态
			ActivityService.setActivityStatus(map, now);
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
	public List<Map<String, Object>> participateActivity(String userId) {
		Map<String, Object> userAndActivity=new HashMap<String,Object>();
		userAndActivity.put("userId", userId);
		List<Map<String, Object>> activityList = userMapper.participateActivity(userAndActivity);
		List<String> activityIds = new ArrayList<String>();
		long now = System.currentTimeMillis();
		for (Map<String, Object> map : activityList) {
			System.out.println(String.valueOf(map.get("activity_id")) + "+:activityId in");
			activityIds.add(String.valueOf(map.get("activity_id")));
			// 设置活动状态
			ActivityService.setActivityStatus(map, now);
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
	public List<Map<String, Object>> attentionActivity(String userId) {
		Map<String, Object> userAndActivity=new HashMap<String,Object>();
		userAndActivity.put("userId", userId);
		List<Map<String, Object>> activityList = userMapper.attentionActivity(userAndActivity);
		List<String> activityIds = new ArrayList<String>();
		long now = System.currentTimeMillis();
		for (Map<String, Object> map : activityList) {
			System.out.println(String.valueOf(map.get("activity_id")) + "+:activityId in");
			activityIds.add(String.valueOf(map.get("activity_id")));
			// 设置活动状态
			ActivityService.setActivityStatus(map, now);
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
	@Transactional
	public void commentActivity(Map<String, Object> result) {
		Map<String,Object> comment=new HashMap<String,Object>();
		try {
			String commentId = RSAUtils.md5(result.get("userId") + String.valueOf(System.currentTimeMillis()));
			//1.创建评论
			comment.put("comment_id", commentId);
			comment.put("comment_desc", result.get("comment"));
			comment.put("comment_creator", result.get("userId"));
			comment.put("comment_object", result.get("commentObject"));
			userMapper.saveComment(comment);
			//保存评论与图片关系
			List<Map<String, Object>> fileNameList=(List<Map<String, Object>>) result.get("fileList");
			if(fileNameList.size()>0) {
				List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> picsAndComment = new ArrayList<Map<String, Object>>();

				for (Map<String, Object> fileName:fileNameList) {

					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, Object> mapCommentAndPic = new HashMap<String, Object>();
					map.put("pic_id", RSAUtils.md5(fileName.get("newname").toString()));
					map.put("pic_name", fileName.get("newname"));
					map.put("pic_creator", fileName.get("userid"));
					map.put("pic_activity_id", result.get("commentObject"));
					map.put("pic_face", false);
					mapCommentAndPic.put("comment_and_pic_pic_id", map.get("pic_id"));
					mapCommentAndPic.put("comment_and_pic_comment_id", commentId);
					pics.add(map);
					picsAndComment.add(mapCommentAndPic);
				}
				activityMapper.savePic(pics);
				userMapper.saveCommentAndPic(picsAndComment);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}

}
