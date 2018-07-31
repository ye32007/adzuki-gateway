package com.adzuki.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;

@Configuration
public class DubboConfig {
	
	@Autowired
	private Environment env; 
	
	@Bean("applicationConfig")
	public ApplicationConfig ac(){
		String name =  env.getProperty("spring.dubbo.application.name");
		return new ApplicationConfig(name);
	}
	
	@Bean("registryConfig")
	public RegistryConfig rc(){
		String address = env.getProperty("spring.dubbo.registry.address");
		return new RegistryConfig(address);
	}
	

}
