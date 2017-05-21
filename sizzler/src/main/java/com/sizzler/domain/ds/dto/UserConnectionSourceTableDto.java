package com.sizzler.domain.ds.dto;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.UserConnectionSourceTable;
/**
 * Table实体类
 */
public class UserConnectionSourceTableDto extends UserConnectionSourceTable {

  private static final long serialVersionUID = -3845680144669243773L;
  private static final String delim = ",";
  
  private List<UserConnectionSourceTableColumnDto> columns;//Table下的列列表
  
  private List<Integer> ignoreRowList;//忽略行的列表
  private List<Integer> ignoreColList;//忽略列的列表

  public List<UserConnectionSourceTableColumnDto> getColumns() {
    return columns;
  }

  public void setColumns(List<UserConnectionSourceTableColumnDto> columns) {
    this.columns = columns;
  }

  public List<Integer> getIgnoreRowList() {
    if(CollectionUtil.isNotEmpty(ignoreRowList)){
      return ignoreRowList;
    }
    ignoreRowList = new LinkedList<Integer>();
    String ignoreRowStr = getIgnoreRow();
    if(StringUtil.isNotBlank(ignoreRowStr)){
      //如果忽略行字符串不为空，将字符串转为list
      String [] ignoreRowStringArray = ignoreRowStr.split(delim);
      return stringArrayToIntegerList(ignoreRowStringArray);
    }
    return ignoreRowList;
  }

  public void setIgnoreRowList(List<Integer> ignoreRowList) {
    this.ignoreRowList = ignoreRowList;
  }

  public List<Integer> getIgnoreColList() {
    if(CollectionUtil.isNotEmpty(ignoreColList)){
      return ignoreColList;
    }
    ignoreColList = new LinkedList<Integer>();
    String ignoreColStr = getIgnoreCol();
    if(StringUtil.isNotBlank(ignoreColStr)){
      //如果忽略列字符串不为空，将字符串转为list
      String [] ignoreColStringArray = ignoreColStr.split(delim);
      return stringArrayToIntegerList(ignoreColStringArray);
    }
    return ignoreColList;
  }

  public void setIgnoreColList(List<Integer> ignoreColList) {
    this.ignoreColList = ignoreColList;
  }

  public UserConnectionSourceTableDto(){
  }
  
  public UserConnectionSourceTableDto(UserConnectionSourceTable table){
    if(table != null){
      BeanUtils.copyProperties(table, this);
    }
  }
  /**
   * 子类转换为父类
   */
  public UserConnectionSourceTable parseToTable(){
    UserConnectionSourceTable table = new UserConnectionSourceTable();
    BeanUtils.copyProperties(this, table);
    //暂时不设置具体的忽略行坐标
    //忽略列列表转换为字符串
//    table.setIgnoreRow(StringUtil.collectionToDelimitedString(getIgnoreRowList(), delim));
    
    //忽略行列表转换为字符串
    table.setIgnoreCol(StringUtil.collectionToDelimitedString(getIgnoreColList(), delim));
    return table;
  }
  
  /**
   * StringList必须是数值类型，如果类型不对就报错
   */
  public static List<Integer> stringArrayToIntegerList(String[] stringArray){
    List<Integer> integerList = new LinkedList<Integer>();
    if(stringArray == null || stringArray.length == 0){
      return integerList;
    }
    for(String str : stringArray){
      integerList.add(Integer.valueOf(str));
    }
    return integerList;
  }
}
