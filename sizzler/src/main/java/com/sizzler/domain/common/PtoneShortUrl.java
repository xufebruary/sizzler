package com.sizzler.domain.common;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

/**
 * 短链映射表
 * 
 * @date: 2016年12月14日
 * @author peng.xu
 */
public class PtoneShortUrl implements Serializable {

  private static final long serialVersionUID = 5523443347885221070L;

  @PK
  private String shortKey;
  private String domain;
  private String url;
  private String createTime;
  private String modifyTime;
  private Integer isDelete;

  public String getShortKey() {
    return shortKey;
  }

  public void setShortKey(String shortKey) {
    this.shortKey = shortKey;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
  }

  public Integer getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(Integer isDelete) {
    this.isDelete = isDelete;
  }

}
