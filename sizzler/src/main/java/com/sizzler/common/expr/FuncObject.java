package com.sizzler.common.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;

/**
 * 函数对象
 * 
 * @date: 2016年8月5日
 * @author peng.xu
 */
public class FuncObject implements Serializable {

  private static final long serialVersionUID = -590852913251609771L;

  private String func; // 函数类型 ： SUM、AVG 等
  private String dbCode; // 数据库code： mysql等
  private String expr; // 函数表达式
  private List<String> params = new ArrayList<String>(); // 参数列表(字符串表达式)
  private String compiledExpr; // 编译后的函数表达式

  /**
   * 校验FuncObject是否有效检查： 1、检查聚合函数嵌套使用
   * 
   * @return
   * @date: 2016年8月5日
   * @author peng.xu
   */
  public boolean isValidate() {

    // 检查聚合函数嵌套使用
    List<String> groupFuncList = Arrays.asList(DataBaseConfig.groupFuncArray);
    if (groupFuncList.contains(func.toUpperCase())) {
      for (String param : this.params) {
        for (String f : groupFuncList) {
          if (StringUtil.isNotBlank(param) && param.toUpperCase().contains(f)) {
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * 编译函数表达式，返回编译后的函数表达式
   * 
   * @return
   * @date: 2016年8月5日
   * @author peng.xu
   */
  public String compile() {
    // 目前支持的所有函数都为一个参数，暂时不考虑多参数和无参数情况
    if (!CollectionUtil.isEmpty(this.params)) {
      String param = this.params.get(0);
      this.compiledExpr = DataBaseConfig.buildCalculateColumn(dbCode, param, this.func, false);
    }
    return this.compiledExpr;
  }

  // //////////////////////////////////////////////////////////////

  public String getFunc() {
    return func;
  }

  public void setFunc(String func) {
    this.func = func;
  }

  public String getDbCode() {
    return dbCode;
  }

  public void setDbCode(String dbCode) {
    this.dbCode = dbCode;
  }

  public String getExpr() {
    return expr;
  }

  public void setExpr(String expr) {
    this.expr = expr;
  }

  public String getCompiledExpr() {
    return compiledExpr;
  }

  public void setCompiledExpr(String compiledExpr) {
    this.compiledExpr = compiledExpr;
  }

  public List<String> getParams() {
    return params;
  }

  public void setParams(List<String> params) {
    this.params = params;
  }

}
