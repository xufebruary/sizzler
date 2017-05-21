package com.sizzler.system.api.config;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import com.sizzler.system.Constants;

import javax.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: ApiVesrsionCondition
 * @Description:.
 * @Company: Copyright (c) Ptmind
 * @version: 1.0
 * @date: 2016/12/29
 * @author: zhangli
 */
public class ApiVesrsionCondition implements RequestCondition<ApiVesrsionCondition> {

	// 路径中版本的前缀， 这里用 /v[1-9]/的形式
	private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("/"+Constants.API_VERSION_PERFIX+"(\\d+)/");

	private int apiVersion;

	public ApiVesrsionCondition(int apiVersion){
		this.apiVersion = apiVersion;
	}

	@Override
	public ApiVesrsionCondition combine(ApiVesrsionCondition apiVesrsionCondition) {
		// 采用最后定义优先原则，则方法上的定义覆盖类上面的定义
		return new ApiVesrsionCondition(apiVesrsionCondition.getApiVersion());
	}

	@Override
	public ApiVesrsionCondition getMatchingCondition(HttpServletRequest httpServletRequest) {
		Matcher m = VERSION_PREFIX_PATTERN.matcher(httpServletRequest.getPathInfo());
		if(m.find()){
			Integer version = Integer.valueOf(m.group(1));
			if(version >= this.apiVersion) // 如果请求的版本号大于配置版本号， 则满足
				return this;
		}
		return null;
	}

	@Override
	public int compareTo(ApiVesrsionCondition apiVesrsionCondition, HttpServletRequest httpServletRequest) {
		// 优先匹配最新的版本号
		return apiVesrsionCondition.getApiVersion() - this.apiVersion;
	}

	public int getApiVersion() {
		return apiVersion;
	}
}
