package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		// 这是一个配置类，在这里启动项目。启动后，底层会启动tomcat，同时会创建spring容器，容器会自动扫描bean并将其装入容器中
		SpringApplication.run(CommunityApplication.class, args);
	}

}
