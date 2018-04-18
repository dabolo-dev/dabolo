package com.smartions.dabolo.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.UserMapper;
import com.smartions.dabolo.model.Third;
import com.smartions.dabolo.model.Token;
import com.smartions.dabolo.model.User;
import com.smartions.dabolo.utils.AES;
import com.smartions.dabolo.utils.RSAUtils;

import net.sf.json.JSONObject;

@Service
public class UserService implements IUserService {
	@Autowired
	private UserMapper userMapper;

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
						String toekn = tokenService.createToken(privateStr, userId,
								user.get(User.PUBLIC_STR).toString());
						response.addHeader("toekn", toekn);
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

}
