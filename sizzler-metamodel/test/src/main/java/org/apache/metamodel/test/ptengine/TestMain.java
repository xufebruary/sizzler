package org.apache.metamodel.test.ptengine;

import java.util.HashMap;
import java.util.Map;

import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.ptapp.PtappDataContext;
import org.apache.metamodel.ptengine.PtengineDataContext;
import org.apache.metamodel.test.TestUtil;
import org.apache.metamodel.util.CommonQueryRequest;

public class TestMain {

  public static void main(String[] args) {
    testAppDataContext();
  }

  public static void testPtengineDataContext() {
    CommonQueryRequest queryRequest = new CommonQueryRequest();
    queryRequest.setDimensions("pt:date,pt:browserVer");
    queryRequest.setMetrics("pt:sessions");
    queryRequest.setStartDate("2016-02-01");
    queryRequest.setEndDate("2016-02-29");
    queryRequest.setSort("pt:date");

    String profileId = "526c4480";
    String ptQueryUrl = "http://ptesthquery.ptmind.com/wa/dm/v1_0/data";

    Map<String, String> metricsDataTypeMap = new HashMap<>();
    metricsDataTypeMap.put("pt:sessions", "LONG");

    PtengineDataContext dataContext = new PtengineDataContext(queryRequest, profileId, ptQueryUrl);
    dataContext.setMetricsDataTypeMap(metricsDataTypeMap);

    String query = "select * from " + PtengineDataContext.defaultSchemaName + "." + profileId;

    DataSet dataSet = dataContext.executeQuery(query);

    TestUtil.printDataSet(dataSet);

  }

  public static void testAppDataContext() {
    CommonQueryRequest queryRequest = new CommonQueryRequest();
    queryRequest.setDimensions("pt:date,pt:browserVer");
    queryRequest.setMetrics("pt:sessions,pt:users");
    queryRequest.setStartDate("2016-05-31");
    queryRequest.setEndDate("2016-05-31");
    queryRequest.setSort("pt:date");

    String profileId = "wangwang";
    String ptQueryUrl = "https://apiquery.ptengine.cn/app/v1_0/data";

    Map<String, String> metricsDataTypeMap = new HashMap<>();
    metricsDataTypeMap.put("pt:sessions", "LONG");
    metricsDataTypeMap.put("pt:users", "PERCENT");

    PtappDataContext dataContext = new PtappDataContext(queryRequest, profileId, ptQueryUrl);
    dataContext.setMetricsDataTypeMap(metricsDataTypeMap);

    String query = "select * from " + PtappDataContext.defaultSchemaName + "." + profileId;

    String totalQuery =
        "select pt:sessions,pt:sessions,pt:users,pt:sessions from "
            + PtappDataContext.defaultSchemaName + "." + profileId;

    DataSet dataSet = dataContext.executeQuery(query);

    DataSet totalDataSet = dataContext.executeTotalQuery();


    TestUtil.printDataSet(dataSet);

    TestUtil.printDataSet(totalDataSet);

  }
}
