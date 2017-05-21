package com.sizzler.domain.ds.vo;

import java.io.Serializable;

/**
 * @ClassName: TimezoneVo
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2017/3/16
 * @author: zhangli
 */
public class TimezoneVo implements Serializable{

	private static final long serialVersionUID = -1282948879833090527L;

	private String name;
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
