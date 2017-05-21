package org.apache.metamodel.ptutil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

  private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

  private static String defaultChartSet = "utf-8";
  private static ConnectionConfig connectionConfig;
  // private static PoolingHttpClientConnectionManager cm = new
  // PoolingHttpClientConnectionManager();
  private static RequestConfig requestConfig;
  private static Integer connectTimeout = 60000;
  private static Integer socketTimeout = 150000;

  static {
    // connectionConfig = ConnectionConfig.custom().setBufferSize(4128).build();
    // cm.setDefaultMaxPerRoute(40);
    // cm.setMaxTotal(500);
    // cm.setDefaultConnectionConfig(connectionConfig);
    requestConfig =
        RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout)
            .build();
  }

  public static CloseableHttpClient getHttpClient() {
    // return HttpClients.createMinimal(cm);
    CloseableHttpClient httpClient =
        HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    return httpClient;
  }

  public static String doGet(String url, Map<String, String> params) {
    return doGet(url, params, defaultChartSet);
  }

  public static String doPost(String url, Map<String, String> params) {
    return doPost(url, params, defaultChartSet);
  }


  public static String doGet(String url, Map<String, String> requestParams, String chartSet) {
    if (StringUtil.isBlank(url)) {
      return null;
    }
    try {
      if (requestParams != null && !requestParams.isEmpty()) {
        List<NameValuePair> paramsPairs = new ArrayList<NameValuePair>(requestParams.size());
        for (Entry<String, String> entry : requestParams.entrySet()) {
          String key = entry.getKey();
          String value = entry.getValue();
          if (value != null) {
            paramsPairs.add(new BasicNameValuePair(key, value));
          }
        }

        url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(paramsPairs, chartSet));

      }

      logger.info("url:" + url);
      HttpGet httpGet = new HttpGet(url);
      // httpGet.setHeader("Cookie","ptOneUserEmail=" + requestParams.get("ptOneUserEmail"));
      CloseableHttpResponse response = getHttpClient().execute(httpGet);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        httpGet.abort();
        logger.error("HttpClient,error status code :" + statusCode + " [" + url + "]");
        throw new RuntimeException("HttpClient,error status code :" + statusCode + " [" + url + "]");
      }
      HttpEntity entity = response.getEntity();
      String result = null;
      if (entity != null) {
        result = EntityUtils.toString(entity, chartSet);
      }
      EntityUtils.consume(entity);
      response.close();
      return result;

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

    return null;

  }

  public static String httpPostWithJSON(String url, String json, Map<String, String> requestParams) {
    if (requestParams != null && !requestParams.isEmpty()) {
      List<NameValuePair> paramsPairs = new ArrayList<NameValuePair>(requestParams.size());
      for (Entry<String, String> entry : requestParams.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        if (value != null) {
          paramsPairs.add(new BasicNameValuePair(key, value));
        }
      }

      try {
        url =
            url + "?"
                + EntityUtils.toString(new UrlEncodedFormEntity(paramsPairs, defaultChartSet));
      } catch (IOException e) {
        logger.error(e.getMessage(), e);
      }

    }

    logger.info("url:" + url);
    String result = null;
    try {
      HttpPost httpPost = new HttpPost(url);
      if (null != json && !json.equalsIgnoreCase("")) {
        StringEntity s = new StringEntity(json, "UTF-8"); // 中文乱码在此解决
        s.setContentType("application/json");
        httpPost.setEntity(s);
      }
      CloseableHttpResponse response = getHttpClient().execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        httpPost.abort();
        throw new RuntimeException("HttpClient,error status code :" + statusCode + " [" + url + "]");
      }
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result = EntityUtils.toString(entity);
      }
      EntityUtils.consume(entity);
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String doPost(String url, Map<String, String> params, String chartSet) {
    if (StringUtil.isBlank(url)) {
      return null;
    }
    try {
      List<NameValuePair> pairs = null;
      if (params != null && !params.isEmpty()) {
        pairs = new ArrayList<NameValuePair>(params.size());
        for (Entry<String, String> entry : params.entrySet()) {
          String value = entry.getValue();
          if (value != null) {
            pairs.add(new BasicNameValuePair(entry.getKey(), value));
          }
        }
      }
      HttpPost httpPost = new HttpPost(url);
      if (pairs != null && pairs.size() > 0) {
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, chartSet));
      }
      CloseableHttpResponse response = getHttpClient().execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        httpPost.abort();
        throw new RuntimeException("HttpClient,error status code :" + statusCode + " [" + url + "]");
      }
      HttpEntity entity = response.getEntity();
      String result = null;
      if (entity != null) {
        result = EntityUtils.toString(entity, chartSet);
      }
      EntityUtils.consume(entity);
      response.close();
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("date", "2015-01-01");
    params.put("area", "0");
    params.put("weeks", "1");
    String result =
        HttpClientUtil.doGet("http://localhost:5055/console/rest/ptconsole/getUserWeekPreserve",
            params);
    System.out.println(result);
  }
}
