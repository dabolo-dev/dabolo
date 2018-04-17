package com.smartions.dabolo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartions.dabolo.model.Wechat;
import com.smartions.dabolo.utils.HttpsUtils;

import net.sf.json.JSONObject;

@Service
public class WechatService implements IWechatService {
	@Autowired
	private Wechat wechat;
	private String url="https://api.weixin.qq.com/sns/jscode2session?";
	@Override
	public JSONObject getSessionKey(String code) {
		JSONObject jsonObject= HttpsUtils.sendHtpps("appid="+wechat.getAppId()+"&secret="+wechat.getSecret()+"&js_code="+code+"&grant_type=authorization_code", url);
		return jsonObject;
	}

}
