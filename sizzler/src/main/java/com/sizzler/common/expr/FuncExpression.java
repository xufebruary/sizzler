package com.sizzler.common.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;

public class FuncExpression implements Serializable {

  private static final long serialVersionUID = -1950184201419695620L;

  public static String[] opArray = new String[] { "+", "-", "*", "/", "(", ")" }; // 操作符列表

  private String expr; // 输入表达式
  private String compiledExpr = ""; // 编译后的表达式
  private List<FuncObject> funcList = new ArrayList<FuncObject>(); // 使用到的函数列表（目前只考虑最外层函数，不考虑虑函数嵌套）
  private boolean isCompile = false; // 是否已编译
  private String dbCode; // 数据库code

  public FuncExpression(String expr, String dbCode) {
    this.expr = expr;
    this.dbCode = dbCode;
  }

  /**
   * 复合指标计算公式中是否使用了计算函数
   * 
   * @return
   * @date: 2016年8月4日
   * @author peng.xu
   */
  public static boolean isContainsFunc(String expr) {
    String[] suportFuncArray = DataBaseConfig.suportFuncArray;
    if (StringUtil.isNotBlank(expr)) {
      expr = expr.toUpperCase();
      for (String func : suportFuncArray) {
        if (expr.contains(func.toUpperCase())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 校验表达式使用的函数是否有效
   * 
   * @return
   * @date: 2016年8月5日
   * @author peng.xu
   */
  public boolean isFuncValidate() {
    this.compile(); // 校验前先执行编译
    if (!CollectionUtil.isEmpty(this.funcList)) {
      for (FuncObject func : this.funcList) {
        if (!func.isValidate()) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 编译表达式，解析出所有用到的函数（目前只考虑最外层函数，不考虑虑函数嵌套）
   * 
   * @date: 2016年8月5日
   * @author peng.xu
   */
  public String compile() {
    if (!this.isCompile) {

      if (StringUtil.isNotBlank(this.expr) && FuncExpression.isContainsFunc(this.expr)) {
        List<String> opList = Arrays.asList(FuncExpression.opArray);
        List<String> suportFuncList = Arrays.asList(DataBaseConfig.suportFuncArray);

        Stack<String> funcOpStack = new Stack<String>(); // 函数的操作符栈（左括号，包含函数内部的左括号，用于识别函数括号结束）
        String func = null;
        String funcParamExpr = ""; // 函数的参数表达式
        boolean isFuncParam = false; // 是否为函数参数表达式部分字符串

        String str = ""; // 操作符之间的字符串
        for (int i = 0; i < this.expr.length(); i++) {
          String s = "" + this.expr.charAt(i);
          boolean isFuncOp = false; // 是否为函数的操作符，即函数的左右括号

          if (opList.contains(s)) {
            // 如果是操作符则说明是函数名或操作数的截止位置
            if ("(".equalsIgnoreCase(s)) {
              if (funcOpStack.isEmpty() && suportFuncList.contains(str.toUpperCase())) {
                // 如果栈为空，并且是支持的函数则说明是最外层函数
                func = str.toUpperCase();
                isFuncParam = true;
                isFuncOp = true;
              }
              // 如果是左括号则入栈
              if (isFuncParam) {
                funcOpStack.push("(");
              }
            } else if (")".equalsIgnoreCase(s)) {
              if (isFuncParam) {
                funcOpStack.pop();
                if (funcOpStack.isEmpty()) {
                  isFuncOp = true;
                  isFuncParam = false;
                  FuncObject funcObj = new FuncObject();
                  funcObj.setFunc(func);
                  funcObj.setDbCode(this.getDbCode());
                  funcObj.getParams().add(funcParamExpr);
                  this.funcList.add(funcObj);
                  this.compiledExpr += funcObj.compile();

                  func = null;
                  funcParamExpr = "";
                  isFuncParam = false;
                }
              }
            }

            // 组装编译后的函数表达式
            if (!isFuncParam && !isFuncOp) {
              this.compiledExpr += (str + s);
            }

            str = "";
          } else {
            str += s;
          }

          // 组装函数参数表达式
          if (isFuncParam && !isFuncOp) {
            funcParamExpr += s;
          }

          // 扫描完表达式，将最后的操作数拼接到编译表达
          if (i == this.expr.length() - 1) {
            this.compiledExpr += str;
          }
        }
      } else {
        this.compiledExpr = this.expr;
      }
    }
    this.isCompile = true;
    return this.compiledExpr;
  }

  // //////////////////////////////////////////////////////////////////////

  public String getExpr() {
    return expr;
  }

  public void setExpr(String expr) {
    this.expr = expr;
    this.isCompile = false; // 修改表达式后重新compile
  }

  public String getCompiledExpr() {
    return compiledExpr;
  }

  public void setCompiledExpr(String compiledExpr) {
    this.compiledExpr = compiledExpr;
  }

  public List<FuncObject> getFuncList() {
    return funcList;
  }

  public void setFuncList(List<FuncObject> funcList) {
    this.funcList = funcList;
  }

  public boolean isCompile() {
    return isCompile;
  }

  public void setCompile(boolean isCompile) {
    this.isCompile = isCompile;
  }

  public String getDbCode() {
    return dbCode;
  }

  public void setDbCode(String dbCode) {
    this.dbCode = dbCode;
  }

  public static void main(String[] args) {
    FuncExpression exp = new FuncExpression("sum([a])+10+sum([b])+10", "mysql");
    System.out.println(exp.compile());
  }
}
