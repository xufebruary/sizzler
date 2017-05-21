package com.sizzler.provider.common.util;

public class BuildStringUtil {

  /** 结构使字符(`) */
  public static String DS_SINGLE_QUOTES = "`";
  /** 小数点 */
  public static String THE_DECIMAL_POINT = ".";

  /**
   * 
   * @description 将原sql中的表名替换为库名.表名
   * @author shaoqiang.guo
   * @date 2016年10月18日 下午12:14:42
   * @param querySql
   * @param tableName
   * @param dataBaseName
   * @return
   */
  public static String buildQuerySql(String querySql, String tableName, String dataBaseName) {
    // `tabName`==> `dataBaseName`.`tableName`
    String newTableName =
        dataBaseName + DS_SINGLE_QUOTES + THE_DECIMAL_POINT + DS_SINGLE_QUOTES + tableName;
    if (!querySql.contains(newTableName)) {
      return querySql.replace(tableName, newTableName);
    }
    return querySql;
  }
}
