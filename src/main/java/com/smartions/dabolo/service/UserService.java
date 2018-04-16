package com.smartions.dabolo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartions.dabolo.mapper.UserMapper;
import com.smartions.dabolo.model.User;
import com.smartions.dabolo.utils.AES;
import com.smartions.dabolo.utils.RSAUtils;

@Service
public class UserService implements IUserService {
	@Autowired
	private UserMapper userMapper;

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
			user=userMapper.getUser(userId);
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
	public Map<String, Object> signIn(String userId, String password) {
		Map<String, Object> user=userMapper.getUser(userId);
		try {
			if(userId.equals(RSAUtils.md5(user.get(User.PUBLIC_STR).toString()))) {//验证用户id与公钥匹配。
				String privateStr=AES.decrypt(user.get(User.PRIVATE_STR).toString(), password);
				if(RSAUtils.verify(password, user.get(User.PUBLIC_STR).toString(), RSAUtils.signStr(password, privateStr))) {//验证公钥与解密的私钥是否匹配
					if(Boolean.parseBoolean(user.get(User.ACTIVE).toString())) {
						user.remove(User.PRIVATE_STR);
						user.remove(User.ACTIVE);
						return user;
					}
					
					
					
				}
			}
				
			
		} catch (Exception e) {
			
		}
		return new HashMap<String,Object>();
	}

}
