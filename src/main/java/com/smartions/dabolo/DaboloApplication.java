package com.smartions.dabolo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages="com.smartions.dabolo.mapper")
public class DaboloApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaboloApplication.class, args);
	}
}
