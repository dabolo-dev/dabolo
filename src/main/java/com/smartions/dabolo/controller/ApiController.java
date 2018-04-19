package com.smartions.dabolo.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.smartions.dabolo.service.IActivityService;
import com.smartions.dabolo.service.ITokenService;
import com.smartions.dabolo.service.IUserService;
import com.smartions.dabolo.service.IWechatService;
import com.smartions.dabolo.utils.AESWechat;
import com.smartions.dabolo.utils.RSAUtils;

import net.sf.json.JSONObject;

@RestController
public class ApiController {
	@Autowired
	IUserService userService;

	@Autowired
	IWechatService wechatService;

	@Autowired
	IActivityService activityService;

	@Autowired
	ITokenService tokenService;

	public boolean apiOauth(HttpServletRequest request, HttpServletResponse response) {
		if (request.getHeader("token") == null)
			return false;
		String tokenNew = tokenService.refreshToken(request.getHeader("token"));
		if (tokenNew != null)
			response.addHeader("token", tokenNew);
		return tokenNew != null;
	}

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
			@RequestParam(value = "userid") String userId, HttpServletResponse response) {
		return userService.signIn(userId, password, response);
	}

	@GetMapping(value = "/user/updatepassword")
	public Map<String, Object> updatePassword(@RequestParam(value = "oldpassword") String oldPassword,
			@RequestParam(value = "newpassword") String newPassword, @RequestParam(value = "userid") String userId,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (apiOauth(request, response)) {
			result.put("flag", userService.updatePassword(userId, oldPassword, newPassword));
		} else {
			result.put("flag", 0);
		}

		return result;

	}

	@GetMapping(value = "/activity")
	public List<Map<String,Object>> getActivityList() {
		return activityService.getActivityList();
	}

	@GetMapping(value = "/activityinfo/{id}")
	public Map<String,Object> getActivityInfo(@PathVariable(value = "id") String id) {
		return activityService.getActivityInfo(id);
	}

	@GetMapping(value = "/wechat/connect")
	public Map<String, Object> wechcatConnect(@RequestParam(value = "openid") String openId,
			@RequestParam(value = "unionid", required = false) String unionId, HttpServletResponse response) {

		try {
			return userService.wechatConnect(openId, RSAUtils.md5(openId), response);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping(value = "/wechat/decodedata")
	public Map<String, Object> decodeData(@RequestParam(value = "data") String data,
			@RequestParam(value = "code") String code, @RequestParam(value = "iv") String iv) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject sessionResult = wechatService.getSessionKey(code);
		result.put("unionid", sessionResult.get("unionid"));
		result.put("openid", sessionResult.get("openid"));

		try {

			byte[] resultByte = AESWechat.decrypt(Base64.decodeBase64(data),
					Base64.decodeBase64(sessionResult.get("session_key").toString()), Base64.decodeBase64(iv));
			if (null != resultByte && resultByte.length > 0) {
				String userInfo = new String(resultByte, "UTF-8");
				result.put("data", userInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@PostMapping(value = "/file/upload")
	public Map<String, Object> upload(@RequestParam(value = "userid") String data, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (apiOauth(request, response)) {
			List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

			MultipartFile file = null;

			BufferedOutputStream stream = null;

			for (int i = 0; i < files.size(); ++i) {

				file = files.get(i);

				if (!file.isEmpty()) {

					try {

						byte[] bytes = file.getBytes();

						stream =

								new BufferedOutputStream(new FileOutputStream(new File(file.getOriginalFilename())));

						stream.write(bytes);

						stream.close();

					} catch (Exception e) {

						stream = null;
						result.put("flag", 0);

					}

				} else {
					result.put("flag", -1);
				}

			}
			result.put("flag", 1);

		} else

		{
			result.put("flag", -2);
		}
		return result;
	}

	@GetMapping(value = "/activity/create")
	public Map<String, Object> createActivity(@RequestParam(value = "userid") String userId,@RequestParam(value = "activity") String activity, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (apiOauth(request, response)) {
			
			
			
		} else {

		}
		return result;
	}
}
