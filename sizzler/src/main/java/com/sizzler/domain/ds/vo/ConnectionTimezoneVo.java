package com.sizzler.domain.ds.vo;

import java.io.Serializable;

/**
 * @ClassName: ConnectionTimezoneVo
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2017/3/16
 * @author: zhangli
 */
public class ConnectionTimezoneVo implements Serializable {

	private static final long serialVersionUID = -991806602452786693L;

	private String isDefaultTimezone;//取数默认时区
	private TimezoneVo dataTimezone;//取数的时区设置
	private String supportTimezone;//是否支持时区
	private String hasTimezoneFiled;//是否含有时间戳字段

	public String getHasTimezoneFiled() {
		return hasTimezoneFiled;
	}

	public void setHasTimezoneFiled(String hasTimezoneFiled) {
		this.hasTimezoneFiled = hasTimezoneFiled;
	}

	public String getIsDefaultTimezone() {
		return isDefaultTimezone;
	}

	public void setIsDefaultTimezone(String isDefaultTimezone) {
		this.isDefaultTimezone = isDefaultTimezone;
	}

	public TimezoneVo getDataTimezone() {
		return dataTimezone;
	}

	public void setDataTimezone(TimezoneVo dataTimezone) {
		this.dataTimezone = dataTimezone;
	}

	public String getSupportTimezone() {
		return supportTimezone;
	}

	public void setSupportTimezone(String supportTimezone) {
		this.supportTimezone = supportTimezone;
	}
}
