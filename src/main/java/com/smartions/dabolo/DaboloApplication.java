package com.smartions.dabolo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartions.dabolo.model.UploadFile;

@SpringBootApplication
@MapperScan(basePackages = "com.smartions.dabolo.mapper")
public class DaboloApplication {
	@Autowired
	private UploadFile uploadFile;

	@Bean
	public MultipartConfigElement multipartConfigElement() {

		MultipartConfigFactory factory = new MultipartConfigFactory();

		//// 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;

		factory.setMaxFileSize(uploadFile.getMaxSize()); // KB,MB

		/// 设置总上传数据总大小

		factory.setMaxRequestSize(uploadFile.getRequestSize());

		// Sets the directory location wherefiles will be stored.


		return factory.createMultipartConfig();

	}

	public static void main(String[] args) {
		SpringApplication.run(DaboloApplication.class, args);
	}

}
