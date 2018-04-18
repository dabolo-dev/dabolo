package com.smartions.dabolo.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component  
@ConfigurationProperties(prefix="upload")
public class UploadFile {
	private String path;
	private String maxSize;
	private String requestSize;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}
	public String getRequestSize() {
		return requestSize;
	}
	public void setRequestSize(String requestSize) {
		this.requestSize = requestSize;
	}
}
