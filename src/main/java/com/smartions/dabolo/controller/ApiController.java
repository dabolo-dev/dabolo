package com.smartions.dabolo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartions.dabolo.model.User;
import com.smartions.dabolo.service.IUserService;

@RestController
public class ApiController {
	@Autowired
	IUserService userService;
	@GetMapping(value="/")
	public String test() {
		return "hello world1235";
	}
	
	@GetMapping(value="/{id}")
	public String pathValile(@PathVariable(value="id") int id) {
		return "pathValile:"+id;
	}
	
	@GetMapping(value="/{id}/api")
	public String requestParm(@PathVariable(value="id") int id,@RequestParam(value="index",required=false,defaultValue="0") int index) {
		return "requestParm:"+id+":"+index;
	}
	
	@GetMapping(value="/json")
	public Map<String,String> json(){
		Map<String,String> map=new HashMap<String,String>();
		map.put("response", "这是中文");
		return map;
	}
	@GetMapping(value="/user")
	public List<User> getUserList(){
		return userService.getAllUser();
	}
}
