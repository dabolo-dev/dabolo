package com.smartions.dabolo.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.smartions.dabolo.model.Activity;
import com.smartions.dabolo.service.IActivityService;
import com.smartions.dabolo.service.ITokenService;
import com.smartions.dabolo.service.IUserService;
import com.smartions.dabolo.service.IWechatService;
import com.smartions.dabolo.utils.AESWechat;
import com.smartions.dabolo.utils.GeoHash;
import com.smartions.dabolo.utils.RSAUtils;

import net.sf.json.JSONArray;
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
	
	@Value("${upload.path}")
	private String filePath;

	public boolean apiOauth(HttpServletRequest request, HttpServletResponse response) {
		if (request.getHeader("token") == null)
			return false;
		String tokenNew = tokenService.refreshToken(request.getHeader("token"));
		if (tokenNew != null)
			response.addHeader("token", tokenNew);
		return tokenNew != null;
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
	public List<Map<String, Object>> getActivityList() {
		return activityService.getActivityList();
	}

	@GetMapping(value = "/activityinfo/{id}")
	public Map<String, Object> getActivityInfo(@PathVariable(value = "id") String id) {
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
	@GetMapping(value="/file/delete")
	public Map<String, Object> deleteFile(@RequestParam(value="filename") String fileName, @RequestParam(value="activityid",required=false,defaultValue="-1") String activityId,HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("flag", 0);
		if (apiOauth(request, response)) {
			File file=new File(filePath+fileName);
			if(file.exists()) {
				file.delete();
			}
			//delete from db
			if(!("-1".equals(activityId)||StringUtils.isBlank(activityId))) {
				activityService.deletePicture(fileName);
			}
			result.put("flag", 1);
		}
		return result;
	}

	@PostMapping(value = "/file/upload")
	public Map<String, Object> upload(@RequestParam(value="userid") String userId,HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (apiOauth(request, response)) {
			List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

			MultipartFile file = null;

			BufferedOutputStream stream = null;
			List<Map<String,String>> fileNameList=new ArrayList<Map<String,String>>();

			for (int i = 0; i < files.size(); ++i) {

				file = files.get(i);

				if (!file.isEmpty()) {

					try {
						String fileName=userId+"_"+System.currentTimeMillis()+"_"+file.getOriginalFilename();
						Map<String, String> fileInfo = new HashMap<String, String>();
						fileInfo.put("newName", fileName);
						fileInfo.put("oldName", file.getOriginalFilename());
						File tmeFile = new File(filePath+fileName);
						System.out.println(tmeFile.getAbsolutePath());
						byte[] bytes = file.getBytes();

						stream =

								new BufferedOutputStream(new FileOutputStream(tmeFile));

						stream.write(bytes);

						stream.close();
						
						fileNameList.add(fileInfo);
						
					} catch (Exception e) {

						stream = null;
						result.put("flag", 0);

					}

				} else {
					result.put("flag", -1);
				}

			}
			result.put("flag", 1);
			result.put("fileList", fileNameList);

		} else

		{
			result.put("flag", -2);
		}
		return result;
	}

	@PostMapping(value = "/activity/create")
	public Map<String, Object> createActivity(
			@RequestParam(value="activity") String activity, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			String inData=URLDecoder.decode(URLDecoder.decode(activity,"UTF-8"),"UTF-8");
			System.out.println("indata:"+inData);
			JSONObject json=JSONObject.fromObject(inData);
			System.out.println(json.get("userid"));
			if (apiOauth(request, response)) {
				Map<String, Object> inDataMap = new HashMap<String, Object>();
				String activityId=RSAUtils.md5(json.get("userid") + String.valueOf(System.currentTimeMillis()));
				inDataMap.put(Activity.TITLE, json.getString("name"));
				inDataMap.put(Activity.ID, activityId);
				inDataMap.put(Activity.DESC, json.getString("description"));
				inDataMap.put(Activity.IS_PUBLIC, json.getBoolean("isPublic"));
				inDataMap.put(Activity.ALLOW_PERSION, json.getInt("limit"));
				inDataMap.put(Activity.CREATOR, json.getString("userid"));
				inDataMap.put(Activity.END, json.getString("endtime"));
				inDataMap.put(Activity.CHARGE, json.getString("cost"));
				inDataMap.put(Activity.SIGN_UP_START, json.getString("signupstarttime"));
				inDataMap.put(Activity.SIGN_UP_END, json.getString("signupendtime"));
				inDataMap.put(Activity.START, json.getString("starttime"));
				inDataMap.put(Activity.LOCATION, json.getJSONObject("address").getString("addstr"));
				inDataMap.put(Activity.LOCATION_latitude, json.getJSONObject("address").getDouble("latitude"));
				inDataMap.put(Activity.LOCATION_longitude, json.getJSONObject("address").getDouble("longitude"));
				GeoHash ghash=new GeoHash(json.getJSONObject("address").getDouble("latitude"),json.getJSONObject("address").getDouble("longitude"));
				inDataMap.put(Activity.LOCATION_GEOHASH, ghash.getGeoHashBase32());
				JSONArray lableList=json.getJSONArray("tag");
				List<Map<String,Object>> lables=new ArrayList<Map<String,Object>>();
				for(int i =0;i<lableList.size();i++) {
					Map<String,Object> map=new HashMap<String,Object>();
					map.put("activity_and_lable_activity_id", activityId);
					map.put("activity_and_label_label_name", lableList.getJSONObject(i).getString("name"));
					lables.add(map); 
				}
				
				inDataMap.put("labeList", lables);
				
				List<Map<String,Object>> pics=new ArrayList<Map<String,Object>>();
				
				JSONArray picList=json.getJSONArray("pic");
				for(int i =0;i<picList.size();i++) {
					JSONObject jObject=picList.getJSONObject(i);
					
					Map<String,Object> map=new HashMap<String,Object>();
					
						map.put("pic_id", RSAUtils.md5(jObject.getString("newname")));
						map.put("pic_name", jObject.getString("newname"));
						map.put("pic_creator", json.get("userid"));
						map.put("pic_activity_id", activityId);
						map.put("pic_face", jObject.getBoolean("iscover"));
					
					pics.add(map);
				}
				inDataMap.put("picList", pics);
				JSONArray typeList=json.getJSONArray("atype");
				List<Map<String,Object>> types=new ArrayList<Map<String,Object>>();
				for(int i =0;i<typeList.size();i++) {
					Map<String,Object> map=new HashMap<String,Object>();
					map.put("activity_and_type_activity_id", activityId);
					map.put("activity_and_type_type_id", typeList.getJSONObject(i).getString("id"));
					types.add(map);
				}
				inDataMap.put("typeList", types);
				String flag= String.valueOf(activityService.createActivity(String.valueOf(json.get("userid")), inDataMap).get("flag"));
				System.out.println(flag+":flag");
				if("1".equals(flag)){
					return activityService.getActivityInfo(activityId);
				}
			} else {

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	@GetMapping(value = "/activity/typelist")
	public List<Map<String, Object>> getTypeList(HttpServletRequest request,
			HttpServletResponse response) {
		if (apiOauth(request, response)) {
			return activityService.getTypeList();
		}
		
		return null;
		
	}

	@GetMapping(value = "/activity/defaultlabellist")
	public List<Map<String, Object>> defaultlabellist(HttpServletRequest request,
			HttpServletResponse response) {
		if (apiOauth(request, response)) {
			return activityService.getLabelList();
		}
		return null;
	}
}
