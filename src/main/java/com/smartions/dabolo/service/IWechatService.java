package com.smartions.dabolo.service;

import net.sf.json.JSONObject;

public interface IWechatService {
	public JSONObject getSessionKey(String code);
}
