package com.sizzler.domain.widget;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

/**
 * 模板demo数据实体类
 * 
 * @author peng.xu
 * 
 */
public class PtoneWidgetTempletData implements Serializable {

  private static final long serialVersionUID = -8996536507685513144L;

  @PK
  private String templetId;
  private String data;

  public String getTempletId() {
    return templetId;
  }

  public void setTempletId(String templetId) {
    this.templetId = templetId;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

}
