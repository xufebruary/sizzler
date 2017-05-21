package org.apache.metamodel.test.googlead;

import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.googlead.GoogleAdDataContext;
import org.apache.metamodel.googlead.util.AdwordsReportType;
import org.apache.metamodel.test.TestUtil;
import org.apache.metamodel.util.CommonQueryRequest;
import org.apache.metamodel.util.Oauth2Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ptmind on 2015/12/3.
 */
public class TestMain {
  private static final String CLIENT_ID =
      "1023490303349-dahr768hbl9u2r0ap750tgbidql0l2r0.apps.googleusercontent.com";
  private static final String CLIENT_SECRET = "gq3jTgcPyeOoT5aP9daA0Np3";
  private static final String REFRESH_TOKEN =
      "1/boyCaFjssljA9drqxSm_7GXISMQInIRtPycYCCYW7XJIgOrJDtdun6zK6XiATCKT";
  // 当前这个DEVELOPER_TOKEN 只能用于查询测试帐号的信息
  private static final String DEVELOPER_TOKEN = "PUpM0DXGPRr13E0gR1n9fA";
  private static String userEmail = "andy@ptthink.com";

  public static void main(String[] args) {
    testGoogleAdDataContext();
  }

  public static void testGoogleAdDataContext() {
    Oauth2Token oauth2Token = new Oauth2Token();
    oauth2Token.setClientId(CLIENT_ID);
    oauth2Token.setClientSecret(CLIENT_SECRET);
    oauth2Token.setRefreshToken(REFRESH_TOKEN);

    CommonQueryRequest queryRequest = new CommonQueryRequest();
    queryRequest.setDimensions("Date");
    queryRequest.setMetrics("Clicks,Impressions");
    queryRequest.setStartDate("20140901");
    queryRequest.setEndDate("20161010");

    Map<String, String> metricsDataTypeMap = new HashMap<>();
    metricsDataTypeMap.put("Clicks", "LONG");
    metricsDataTypeMap.put("Impressions", "LONG");
    metricsDataTypeMap.put("Ctr", "DOUBLE");

    String customterId = "1013515661";

    GoogleAdDataContext dataContext =
        new GoogleAdDataContext(oauth2Token, DEVELOPER_TOKEN, customterId, queryRequest, userEmail,
            AdwordsReportType.ACCOUNT_PERFORMANCE_REPORT);
    dataContext.setMetricsDataTypeMap(metricsDataTypeMap);

    String query =
        "select * from " + GoogleAdDataContext.defaultSchemaName + "."
            + AdwordsReportType.ACCOUNT_PERFORMANCE_REPORT.toString();

    // String
    // query="select max(Impressions) from "+GoogleAdDataContext.defaultSchemaName+"."+AdwordsReportType.ACCOUNT_PERFORMANCE_REPORT.toString();

    DataSet dataSet = dataContext.executeQuery(query);

    TestUtil.printDataSet(dataSet);

  }
}
