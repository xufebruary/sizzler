package org.apache.metamodel.util;

import org.apache.metamodel.ptutil.StringUtil;
import org.apache.metamodel.schema.ColumnType;

/**
 * Created by ptmind on 2015/12/3.
 */
public class ColumnTypeUtil {

  public static ColumnType convertToColumnType(String type) {
    if (StringUtil.isBlank(type)) {
      return ColumnType.STRING;
    }
    switch (type.toUpperCase()) {
      case "LONG":
        return ColumnType.BIGINT;
      case "INTEGER":
        return ColumnType.INTEGER;
      case "FLOAT":
      case "DOUBLE":
        return ColumnType.DOUBLE;
      case "NUMBER":
        return ColumnType.NUMERIC;
      case "PERCENT":
        return ColumnType.PERCENT;
      case "CURRENCY":
        return ColumnType.CURRENCY;
        /**
         * 新增时间类型
         * @author shaoqiang.guo
         */
      case "DATETIME":
        return ColumnType.DATETIME;
      case "DATE":
        return ColumnType.DATE;
      case "TIME":
        return ColumnType.TIME;
        /**
         * end
         */
    }
    return ColumnType.STRING;
  }
}
