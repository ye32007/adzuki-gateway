package com.adzuki.gateway.cache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.adzuki.gateway.entity.ApiConfig;
import com.adzuki.gateway.mapper.ApiConfigMapper;

@Component("apiConfigCache")
public class ApiConfigCache implements Cache{
	
	@Autowired
	private ApiConfigMapper apiConfigMapper;
	
	public Map<Long,ApiConfig> apiConfigCache = new LinkedHashMap<Long,ApiConfig>();
	public Map<String,ApiConfig> apiConfigVeriosnCache = new LinkedHashMap<String,ApiConfig>();
	
	@PostConstruct
	public void init(){
		List<ApiConfig> all = apiConfigMapper.selectAll();
		Map<Long,ApiConfig> apiConfigCacheTemp = new LinkedHashMap<Long,ApiConfig>();
		Map<String,ApiConfig> apiConfigVeriosnCacheTemp = new LinkedHashMap<String,ApiConfig>();
		for(ApiConfig config : all){
			apiConfigCacheTemp.put(config.getId(), config);
			apiConfigVeriosnCacheTemp.put(config.getApi() + "_" + config.getVersion(), config);
		}
		apiConfigCache = apiConfigCacheTemp;
		apiConfigVeriosnCache = apiConfigVeriosnCacheTemp;
	}

	@Override
	public void reload() {
		init();
	}

}
