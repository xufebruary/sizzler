package com.sizzler.provider.common;
/**
 * 来源类型枚举
 * @author you.zou
 * @date 2016年10月28日 下午2:55:02
 */
public enum SourceType {
  /**
   * 页面
   */
  PAGES("PAGES"),
  /**
   * 自动更新
   */
  AUTO_UPDATE("AUTO_UPDATE");
  
  private final String value;
  
  SourceType(String value){
    this.value = value;
  }
  
  public String getValue(){
    return value;
  }
}
