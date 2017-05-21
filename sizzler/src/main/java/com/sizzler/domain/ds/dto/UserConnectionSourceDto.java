package com.sizzler.domain.ds.dto;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.sizzler.domain.ds.UserConnectionSource;

/**
 * Source的实体类
 */
public class UserConnectionSourceDto extends UserConnectionSource {

  private static final long serialVersionUID = -5409035865280763616L;

  private List<UserConnectionSourceTableDto> tables;// Source下的表列表

  public List<UserConnectionSourceTableDto> getTables() {
    return tables;
  }

  public void setTables(List<UserConnectionSourceTableDto> tables) {
    this.tables = tables;
  }

  public UserConnectionSourceDto() {
  }

  public UserConnectionSourceDto(UserConnectionSource source) {
    if (source != null) {
      BeanUtils.copyProperties(source, this);
    }
  }

  /**
   * 子类转换为父类
   */
  public UserConnectionSource parseToSource() {
    UserConnectionSource source = new UserConnectionSource();
    BeanUtils.copyProperties(this, source);
    return source;
  }

}
