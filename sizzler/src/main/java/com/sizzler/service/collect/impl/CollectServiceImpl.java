package com.sizzler.service.collect.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.service.collect.CollectService;

@Service("collectService")
public class CollectServiceImpl implements CollectService {

  private static final Logger log = Logger.getLogger(CollectServiceImpl.class);

  @Override
  public void saveAppCollectInfo(Map<String, String> infoMap) {
    if (CollectionUtil.isNotEmpty(infoMap)) {
      try {
        List<String> columnList = new ArrayList<String>();
        List<String> valueList = new ArrayList<String>();
        for (Map.Entry<String, String> entry : infoMap.entrySet()) {
          columnList.add(entry.getKey());
          valueList.add(fixColValue(entry.getValue()));
        }
        StringBuilder sqlBuilder = new StringBuilder("insert into sizzler_test.app_collect_info ");
        sqlBuilder.append(" (`").append(StringUtil.join(columnList, "`, `")).append("`) ");
        sqlBuilder.append(" values ");
        sqlBuilder.append(" ('").append(StringUtil.join(valueList, "', '", false, false))
            .append("') ;");

        Object param[] = {};

        String sql = sqlBuilder.toString();
        log.info(" >>> insert sql : " + sql);
        DataOperationUtils.insert(sql, param);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw e;
      }
    }
  }

  /**
   * 处理ColValue数据
   */
  private static String fixColValue(String colValue) {
    if (colValue.contains("\\")) {
      colValue = colValue.replaceAll("\\\\", "\\\\\\\\'");
    }
    if (colValue.contains("'")) {
      colValue = colValue.replaceAll("'", "\\\\'");
    }
    return colValue;
  }

}
