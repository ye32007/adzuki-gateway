package com.adzuki.gateway.web.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.adzuki.gateway.cache.ApiConfigCache;
import com.adzuki.gateway.common.data.Result;
import com.adzuki.gateway.entity.ApiConfig;
import com.adzuki.gateway.service.ApiConfigService;
import com.adzuki.gateway.web.controller.util.NetworkUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

@Controller
public class GetewayController {
	
	private Logger logger = LoggerFactory.getLogger(GetewayController.class);
	
	@Autowired
	private ApiConfigCache apiConfigCache;
	
	@Autowired
	private ApiConfigService apiConfigService;
	
	@Autowired
    private RedisTemplate<String, ?> redisTemplate;

	@Value("${gateway.domain}")
	private String domain;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gateway")
	@ResponseBody
	public Result<?> mpi(String api, String version, @RequestParam("data") String data, String timestamp,
			String appCode, String appType, @CookieValue(value = "_ticket", required = false) String ticket,
			HttpServletRequest request,HttpServletResponse response) {
		logger.info("api={}, version={}, data={}, timestamp={}, appCode={}, appType={}", new Object[] { api, version, data, timestamp, appCode, appType});
		if (StringUtils.isBlank(api)) {
			return Result.createFailResult("参数api不能为空");
		}
		if("checkLogin".equals(api)) {
			return checkLogin(ticket);
		}
		if (StringUtils.isBlank(version)) {
			return Result.createFailResult("参数version不能为空");
		}
		ApiConfig config = apiConfigCache.apiConfigVeriosnCache.get(api + "_" + version) ;//apiConfigMapper.selectOne(ApiConfig.builder().api(api).version(version).build());
		if (config == null) {
			return Result.createFailResult("服务" + api + ":" + version + "不存在");
		}
		Map<?, ?> userInfo = null;
		if (StringUtils.isNotBlank(ticket)) {
			try {
				logger.info("_ticket is :" + ticket);
				userInfo = redisTemplate.opsForHash().entries("auth_ticket:" + ticket);
			} catch (Exception e) {
				logger.error("根据_ticket查找当前用户信息失败,_ticket" + ticket, e);
			}
		}
		if (userInfo == null && config.getNeedLogin() == 1) {
			return Result.createFailResult("用户未登录");
		}
		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(data)) {
			try {
				param = JSON.parseObject(data, Map.class);
			} catch (Exception e) {
				logger.error("请求参数反序列化失败,data=" + data, e);
				return Result.createFailResult("参数data格式不正确！");
			}
		} else {
			param = Maps.newHashMap();
		}
		if (userInfo != null && !userInfo.isEmpty()) {
			param.put("currentUserId", Long.parseLong((String)userInfo.get("id")));
			param.put("currentUserName", userInfo.get("username"));
			param.put("currentUserMobile", userInfo.get("mobile"));
		}
		param.put("appCode", appCode);
		param.put("appType", appType);
		try {
			String ip = NetworkUtil.getIpAddress(request);
			param.put("ip", ip);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Map<String, Object> serviceResult = apiConfigService.callService(config, param);
			Result<?> result = new Result<>(serviceResult);
			if ("mobile.login".equals(api)){
				if(result.success()) {
					Cookie cookie = new Cookie("_ticket", (String) result.getData());
					cookie.setDomain(domain);
					logger.info("add cookie domain={}", cookie.getDomain());
					cookie.setHttpOnly(true);
					cookie.setPath("/");
					cookie.setMaxAge(60 * 60 * 24 * 30);
					response.addCookie(cookie);
				}
			} else if ("mobile.logout".equals(api)) {
				if(result.success()){
					Cookie cookie = new Cookie("_ticket", "");
					cookie.setDomain(domain);
					logger.info("del cookie domain={}", cookie.getDomain());
					cookie.setHttpOnly(true);
					cookie.setPath("/");
					cookie.setMaxAge(60 * 30);
					response.addCookie(cookie);
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("服务调用失败,api=" + api, e);
			return Result.createFailResult("服务" + api + ":" + version + "调用失败！");
		}
	}
	
	public Result<?> checkLogin(String ticket){
		if(StringUtils.isBlank(ticket)) {
			return Result.createFailResult("未登陆！");
		}
		try {
			logger.info("_ticket is :" + ticket);
			Map<?, ?> userInfo = redisTemplate.opsForHash().entries("auth_ticket:"+ ticket);
			if(userInfo == null || userInfo.isEmpty()) {
				return Result.createFailResult("未登陆！");
			} else {
				return Result.createSuccessResult(userInfo);
			}
		} catch (Exception e) {
			logger.error("根据_ticket查找当前用户信息失败,_ticket" + ticket, e);
			return Result.createFailResult("查询失败！");
		}
	}
			
}
