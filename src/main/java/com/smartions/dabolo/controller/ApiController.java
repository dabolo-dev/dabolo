package com.smartions.dabolo.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartions.dabolo.service.IUserService;
import com.smartions.dabolo.service.IWechatService;
import com.smartions.dabolo.utils.Encryptor;

import net.sf.json.JSONObject;

@RestController
public class ApiController {
	@Autowired
	IUserService userService;

	@Autowired
	IWechatService wechatService;

	@GetMapping(value = "/")
	public String test() {
		return "hello world1235";
	}

	@GetMapping(value = "/{id}")
	public String pathValile(@PathVariable(value = "id") int id) {
		return "pathValile:" + id;
	}

	@GetMapping(value = "/{id}/api")
	public String requestParm(@PathVariable(value = "id") int id,
			@RequestParam(value = "index", required = false, defaultValue = "0") int index) {
		return "requestParm:" + id + ":" + index;
	}

	@GetMapping(value = "/json")
	public Map<String, String> json() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", "这是中文");
		return map;
	}

	@GetMapping(value = "/user/signup")
	public Map<String, Object> signUp(@RequestParam(value = "password") String password) {
		return userService.signUp(password);
	}

	@GetMapping(value = "/user/signin")
	public Map<String, Object> signIn(@RequestParam(value = "password") String password,
			@RequestParam(value = "userid") String userId) {
		return userService.signIn(userId, password);
	}

	@GetMapping(value = "/user/updatepassword")
	public Map<String, Object> updatePassword(@RequestParam(value = "oldpassword") String oldPassword,
			@RequestParam(value = "newpassword") String newPassword, @RequestParam(value = "userid") String userId) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("flag", userService.updatePassword(userId, oldPassword, newPassword));
		return result;

	}

	@GetMapping(value = "/wechat/connect")
	public Map<String, Object> wechcatConnect(@RequestParam(value = "openid") String openId,
			@RequestParam(value = "unionid") String unionId) {
		return userService.wechatConnect(openId, unionId);
	}

	@GetMapping(value = "/wechat/decodedata")
	public Map<String, Object> decodeData(@RequestParam(value = "data") String data,
			@RequestParam(value = "code") String code, @RequestParam(value = "iv") String iv) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject sessionResult = wechatService.getSessionKey(code);
		result.put("unionid", sessionResult.get("unionid"));
		result.put("openid", sessionResult.get("openid"));
		String key = new String(Base64.decodeBase64(sessionResult.get("session_key").toString()));
		String decodeIv = new String(Base64.decodeBase64(iv));
		String decodeData = new String(Base64.decodeBase64(data));
		String resultData = Encryptor.decrypt(key, decodeIv, decodeData);
		result.put("data", resultData);

		return result;
	}
}
