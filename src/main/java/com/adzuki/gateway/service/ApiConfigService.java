package com.adzuki.gateway.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adzuki.gateway.entity.ApiConfig;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.google.common.collect.Maps;

@Service
public class ApiConfigService {
	
	private final Logger logger = LoggerFactory.getLogger(ApiConfigService.class);
	
	private Map<String, ReferenceConfig<GenericService>> refs = Maps.newConcurrentMap();
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private RegistryConfig registryConfig;
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> callService(ApiConfig config, Map<String, Object> param) {
		ReferenceConfig<GenericService> reference = this.refs.get(config.getDubboServiceName() + ":" + config.getDubboServiceVersion());
		if (reference == null) {
			reference = new ReferenceConfig<GenericService>(); // 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
			reference.setInterface(config.getDubboServiceName()); // 弱类型接口名
			reference.setVersion(config.getDubboServiceVersion());
			reference.setApplication(this.applicationConfig);
			reference.setRegistry(this.registryConfig);
			reference.setGeneric(true); // 声明为泛化接口
			reference.setTimeout(10000);
			refs.put(config.getDubboServiceName() + ":" + config.getDubboServiceVersion(), reference);
		}
		Map<String, Object> result = null;
		GenericService genericService = reference.get(); // 用com.alibaba.dubbo.rpc.service.GenericService可以替代所有接口引用
		if(genericService == null) {
			refs.remove(config.getDubboServiceName() + ":" + config.getDubboServiceVersion());
			result = Maps.newHashMap();
			result.put("code", 500);
			result.put("msg", config.getDubboServiceName() + ":" + config.getDubboServiceVersion() + " 未找到！");
		}
		try {
			result = (Map<String, Object>) genericService.$invoke(config.getDubboServiceMethod(), new String[] { config.getParamClass() }, new Object[] { param });
			removeClass(result);
			logger.info("mapi " + config.getApi() + " is called ,result = " + result);
		} catch (Exception e) {
			logger.error("后端服务调用错误,mpi=" + config.toString(), e);
			result = Maps.newHashMap();
			result.put("code", 500);
			result.put("msg", "网络开小差了~请稍后重试");
		}
		return result;
	}

	private void removeClass(Object serviceResult) {
		if (serviceResult == null) {
			return;
		}
		if (serviceResult instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) serviceResult;
			map.remove("class");

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				removeClass(entry.getValue());
			}
		} else if (serviceResult instanceof Iterable<?>) {
			for (Object o : (Iterable<?>) serviceResult) {
				removeClass(o);
			}
		}
	}

}
