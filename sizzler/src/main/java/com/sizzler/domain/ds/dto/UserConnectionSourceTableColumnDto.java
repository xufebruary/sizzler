package com.sizzler.domain.ds.dto;

import org.springframework.beans.BeanUtils;

import com.sizzler.domain.ds.UserConnectionSourceTableColumn;

/**
 * Column的实体类
 */
public class UserConnectionSourceTableColumnDto extends UserConnectionSourceTableColumn {

  private static final long serialVersionUID = 2308161775058401840L;

  public UserConnectionSourceTableColumnDto() {

  }

  public UserConnectionSourceTableColumnDto(UserConnectionSourceTableColumn column) {
    if (column != null) {
      BeanUtils.copyProperties(column, this);
    }
  }

  /**
   * 子类转换为父类
   */
  public UserConnectionSourceTableColumn parseToColumn() {
    UserConnectionSourceTableColumn column = new UserConnectionSourceTableColumn();
    BeanUtils.copyProperties(this, column);
    return column;
  }

}
