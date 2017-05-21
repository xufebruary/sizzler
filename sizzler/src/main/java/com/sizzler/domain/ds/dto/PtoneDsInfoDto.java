package com.sizzler.domain.ds.dto;

import org.springframework.beans.BeanUtils;

import com.sizzler.domain.ds.PtoneDsInfo;

public class PtoneDsInfoDto extends PtoneDsInfo {

  private static final long serialVersionUID = 2090490467970613743L;

  public PtoneDsInfoDto() {}

  public PtoneDsInfoDto(PtoneDsInfo ptoneDsInfo) {
    if (ptoneDsInfo != null) {
      BeanUtils.copyProperties(ptoneDsInfo, this);
    }
  }

  public long getDsId() {
    return this.getId();
  }

  public String getDsCode() {
    return this.getCode();
  }
}
