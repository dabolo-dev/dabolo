package com.smartions.dabolo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartions.dabolo.model.Wechat;
import com.smartions.dabolo.model.WechatMessage;
import com.smartions.dabolo.utils.HttpsUtils;

import net.sf.json.JSONObject;

@Service
public class WechatService implements IWechatService {
	@Autowired
	private Wechat wechat;
	private String url = "https://api.weixin.qq.com/sns/jscode2session?";
	private String access_token = "https://api.weixin.qq.com/cgi-bin/token?";
	private String send_message = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?";

	@Override
	public JSONObject getSessionKey(String code) {
		JSONObject jsonObject = HttpsUtils.sendHtpps("appid=" + wechat.getAppId() + "&secret=" + wechat.getSecret()
				+ "&js_code=" + code + "&grant_type=authorization_code", url);
		return jsonObject;
	}

	@Override
	public boolean sendMessage(WechatMessage message) {
		JSONObject jsonObject = HttpsUtils.sendHtpps("grant_type=client_credential&appid=" + wechat.getAppId() + "&secret=" + wechat.getSecret(),
				access_token);
		if (jsonObject.containsKey("access_token")) {
			String token = jsonObject.getString("access_token");
			JSONObject jsonReturnObject = HttpsUtils.sendHtpps("access_token="+token + "&touser=" + message.getTouser() + "&template_id="
					+ message.getTemplateId() + "&data=" + message.getData() + "&form_id=" + message.getFormId(),
					send_message);
			return jsonReturnObject.containsKey("errmsg") && "".equals(jsonReturnObject.getString("errmsg"));
		}
		return false;
	}

}
