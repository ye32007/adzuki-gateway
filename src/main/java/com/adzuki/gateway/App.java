package com.adzuki.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "com.adzuki" })
@MapperScan(basePackages = { "com.adzuki.gateway.mapper" })
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
