package org.apache.metamodel.test.facebookad;

import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.facebookad.FacebookAdDataContext;
import org.apache.metamodel.test.TestUtil;
import org.apache.metamodel.util.CommonQueryRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ptmind on 2015/12/3.
 */
public class TestMain {
  //
  // public static String
  // ACCESS_TOKEN="CAAJExXZBSFcEBAFCUvlQlWsphjZCZA1i4gq6TTZA5YmVkgBC2F4N1fZAltqH8FXSHp2ivGyln4z214dbhehP1gkCZCzjT9xZBuRhycqFbWeWFyxMLCWcEiD4zZANcIhzO3QoKZBQvB0yqk0ZCydQUvUbkO7kGj39nZBGjxRIgCZBh56nFmEvykNv8HiBsu55V8d48iwZD";
  public static String ACCESS_TOKEN =
      "CAAJExXZBSFcEBANZBufO9ymESfT0GSDlxFcjnPdpoDyjCbCJEXo7T8wFVtyRYWfKlRfRZCSVZAZAq3rtDD8ug3QFCMlmX6eKCPrCjAM5VuSuqOvt0dJUhIzkNBVhxagBAOPauqFo6ngGX924dG9ZCHzVFR0J4fZB6yj38gWGAjauOZBRXgYS5YWLYmJ3PCEZCJhQEVzj4tFA7vgZDZD";

  public static void main(String[] args) {
    testFacebookAdDataContext();
  }

  public static void testFacebookAdDataContext() {
    String objectId = "6029339427270";
    String startDate = "2014-10-07";
    String endDate = "2016-10-07";
    String datePeriod = "day";
    /*
     * String metrics="frequency,spend,total_actions," + "ctr,cpm," +
     * "inline_link_clicks,deeplink_clicks,app_store_clicks,newsfeed_clicks,social_clicks,website_clicks,"
     * + "impressions";
     */
    String metrics = "frequency,spend,total_actions,impressions";
    Map<String, String> metricsDataTypeMap = new HashMap<>();
    metricsDataTypeMap.put("frequency", "DOUBLE");
    metricsDataTypeMap.put("spend", "DOUBLE");
    metricsDataTypeMap.put("total_actions", "INTEGER");
    metricsDataTypeMap.put("impressions", "INTEGER");


    String dimensions = "age";

    CommonQueryRequest queryRequest = new CommonQueryRequest();
    queryRequest.setStartDate(startDate);
    queryRequest.setEndDate(endDate);
    queryRequest.setMetrics(metrics);
    queryRequest.setDimensions(dimensions);

    FacebookAdDataContext dataContext =
        new FacebookAdDataContext(ACCESS_TOKEN, objectId, queryRequest);
    dataContext.setMetricsDataTypeMap(metricsDataTypeMap);
    dataContext.setLevel("account");

    String query =
        "select * from " + FacebookAdDataContext.defaultSchemaName + "."
            + FacebookAdDataContext.defaultTableName;
    // String
    // query="select sum(spend),sum(total_actions),sum(impressions) from "+FacebookAdDataContext.defaultSchemaName+"."+FacebookAdDataContext.defaultTableName;
    DataSet dataSet = dataContext.executeQuery(query);

    TestUtil.printDataSet(dataSet);

  }
}
