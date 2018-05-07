package com.smartions.dabolo.service;

import com.smartions.dabolo.model.WechatMessage;

import net.sf.json.JSONObject;

public interface IWechatService {
	public JSONObject getSessionKey(String code);
	public boolean sendMessage(WechatMessage message);
}
