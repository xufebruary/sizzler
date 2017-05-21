package com.sizzler.metamodel.json;

import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;

public class TestMain {

  public static void main(String[] args) {

    /*
     * String json=
     * "[{\"cancelAtPeriodEnd\":false,\"created-test\":1464961292,\"created\":1464961292,\"currentPeriodEnd\":1467553292,\"currentPeriodStart\":1464961292,\"customer\":\"chonggou_test_lhj11@outlook.com\",\"id\":\"sub_8ZRRI2BzkuLisz\",\"metadata\":{\"billingFrequency\":\"1\",\"billingPeriod\":\"Month\",\"commodityId\":\"200006\",\"commodityName\":\"Lite\",\"orderId\":\"1464961256104790\",\"uid\":\"1456813602454069\",\"userEmail\":\"chonggou_test_lhj11@outlook.com\"},\"plan\":{\"amount\":900,\"created\":1452886283,\"currency\":\"usd\",\"id\":\"200006_1_Month\",\"interval\":\"month\",\"intervalCount\":1,\"livemode\":true,\"metadata\":{\"Plan\":\"Lite\"},\"name\":\"Lite\"},\"quantity\":1,\"start\":1464961292,\"status\":\"active\"},{\"cancelAtPeriodEnd\":false,\"created\":1464961292,\"abc\":1464961292,\"currentPeriodEnd\":1467553292,\"currentPeriodStart\":1464961292,\"customer\":\"chonggou_test_lhj11@outlook.com\",\"id\":\"sub_8ZRRI2BzkuLisz\",\"metadata\":{\"billingFrequency\":\"1\",\"billingPeriod\":\"Month\",\"commodityId\":\"200006\",\"commodityName\":\"Lite\",\"orderId\":\"1464961256104790\",\"uid\":\"1456813602454069\",\"userEmail\":\"chonggou_test_lhj11@outlook.com\"},\"plan\":{\"amount\":900,\"created\":1452886283,\"currency\":\"usd\",\"id\":\"200006_1_Month\",\"interval\":\"month\",\"intervalCount\":1,\"livemode\":true,\"metadata\":{\"Plan\":\"Lite\"},\"name\":\"Lite\"},\"quantity\":1,\"start\":1464961292,\"status\":\"active\",\"id_test\":\"id_test\"}]"
     * ; JsonDataContext jsonDataContext=new JsonDataContext(json, "", "");
     * String query="select created-test from json"; DataSet
     * dataSet=jsonDataContext.executeQuery(query); printDataSet(dataSet);
     */

  }

  public static void printDataSet(DataSet dataSet) {
    while (dataSet.next()) {
      Row row = dataSet.getRow();
      for (Object o : row.getValues()) {
        System.out.print(o + ",");
      }
      System.out.println();
    }

  }
}
