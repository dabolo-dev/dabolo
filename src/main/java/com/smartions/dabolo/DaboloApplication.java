package com.smartions.dabolo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.smartions.dabolo.filter.ApiFilter;

@SpringBootApplication
@MapperScan(basePackages="com.smartions.dabolo.mapper")
public class DaboloApplication {
	public static void main(String[] args) {
		SpringApplication.run(DaboloApplication.class, args);
	}
	
	
}
