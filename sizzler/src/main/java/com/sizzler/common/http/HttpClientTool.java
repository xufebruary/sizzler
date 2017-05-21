package com.sizzler.common.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.sizzler.common.utils.StringUtil;
import com.sizzler.common.utils.UuidUtil;

public class HttpClientTool {
  private static CloseableHttpClient httpClient;
  private static String defaultChartSet = "utf-8";
  // 通过网络与服务器建立连接的超时时间,指的是连接一个url的连接等待时间
  private static Integer connectTimeout = 10 * 1000;
  // Socket读数据的超时时间，即从服务器获取响应数据需要等待的时间;连接上一个url，获取response的返回等待时间
  private static Integer socketTimeout = 150 * 1000;
  private static RequestConfig requestConfig;
  private static PoolingHttpClientConnectionManager connectionManager;
  // 连接池的大小
  private static Integer maxTotal = 400;
  // 每个主机的最多并发
  private static Integer maxPerRoute = 200;

  static {
    requestConfig =
            RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout)
                    .build();
    connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(maxTotal);
    connectionManager.setDefaultMaxPerRoute(maxPerRoute);
    httpClient =
            HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                    .setConnectionManager(connectionManager).build();
  }

  public static String doGet(String url, Map<String, String> params) {
    return doGet(url, params, defaultChartSet);
  }

  public static String doPost(String url, Map<String, String> params, Map<String, String> headers) {
    return doPost(url, params, defaultChartSet, headers);
  }

  public static String doPostJsonWithHeader(String url, String jsonEntity, Map<String, String> headers)
          throws IOException {
    return doPostJson(url, jsonEntity, headers);
  }

  public static String doPostJsonWithoutHeader(String url, String jsonEntity) throws IOException {
    return doPostJsonWithHeader(url, jsonEntity, null);
  }

  public static String doPost(String url, Map<String, String> params) {
    return doPost(url, params, defaultChartSet, null);
  }

  public static String doGet(String url) throws Exception {
    return doGet(url, defaultChartSet);
  }

  public static String doGet(String url, String chartSet) throws Exception {
    if (StringUtil.isBlank(url)) {
      return null;
    }
    HttpGet httpGet = new HttpGet(url);
    CloseableHttpResponse response = httpClient.execute(httpGet);
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode != 200) {
      httpGet.abort();
      throw new RuntimeException("HttpClient,error status code :" + statusCode);
    }
    HttpEntity entity = response.getEntity();
    String result = null;
    if (entity != null) {
      result = EntityUtils.toString(entity, chartSet);
    }
    EntityUtils.consume(entity);
    response.close();
    return result;

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
        System.out.println("url:" + url);
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
          httpGet.abort();
          throw new RuntimeException("HttpClient,error status code :" + statusCode);
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
          result = EntityUtils.toString(entity, chartSet);
        }
        EntityUtils.consume(entity);
        response.close();
        return result;

      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }

    return null;

  }

  public static String doGet(String url, Map<String, String> requestParams, String chartSet,
                             boolean encode) {
    if (StringUtil.isBlank(url)) {
      return null;
    }
    try {
      long startTime = System.currentTimeMillis();
      String uuid = UuidUtil.generateUuid();
      System.out.println(uuid + ">>> start query data ");
      if (requestParams != null && !requestParams.isEmpty()) {
        String params = "";
        if (encode) {
          List<NameValuePair> paramsPairs = new ArrayList<NameValuePair>(requestParams.size());
          for (Entry<String, String> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null) {
              paramsPairs.add(new BasicNameValuePair(key, value));
            }
          }
          params = EntityUtils.toString(new UrlEncodedFormEntity(paramsPairs, chartSet));
        } else {
          StringBuilder paramsBuilder = new StringBuilder();
          int paramPairSize = requestParams.entrySet().size();
          int i = 0;
          for (Entry<String, String> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            paramsBuilder.append(key).append("=").append(value);
            if (i < paramPairSize - 1) {
              paramsBuilder.append("&");
            }
            i++;
          }
          params = paramsBuilder.toString();
        }

        // new StringEntity(paramsPairs, chartSet);

        url = url + "?" + params;
        System.out.println(uuid + ">>> url:" + url);
        HttpGet httpGet = new HttpGet(url);
        // 每个请求单独创建一个http client
        // CloseableHttpClient
        // httpClient=HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        /*
         * if (statusCode != 200) { httpGet.abort(); throw new
         * RuntimeException("HttpClient,error status code :" + statusCode); }
         */
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
          result = EntityUtils.toString(entity, chartSet);
        }
        EntityUtils.consume(entity);
        response.close();

        System.out.println(uuid + ">>> resultCode:" + statusCode + ", response data: " + result);

        System.out.println(uuid + ">>> end query data, cost: "
                + (System.currentTimeMillis() - startTime));

        return result;

      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }

    return null;

  }
  public static String doPost(String url, Map<String, String> params, String chartSet, Map<String, String> headers) {
    if (StringUtil.isBlank(url)) {
      return null;
    }
    try {
      List<NameValuePair> pairs = null;
      if (params != null && !params.isEmpty()) {
        pairs = new ArrayList<NameValuePair>(params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
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

      if (headers != null) {
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
          httpPost.setHeader(key, headers.get(key));
        }
      }

      CloseableHttpResponse response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        httpPost.abort();
        throw new RuntimeException("HttpClient,error status code :" + statusCode);
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


  public static String doPostJson(String url, String jsonEntity, Map<String, String> headers) throws IOException {
    if (StringUtil.isBlank(url)) {
      return null;
    }
    HttpPost httpPost = new HttpPost(url);
    StringEntity input = null;
    try {
      input = new StringEntity(jsonEntity);
      input.setContentType("application/json");
      httpPost.setEntity(input);
      if (headers != null) {
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
          httpPost.setHeader(key, headers.get(key));
        }
      }
      CloseableHttpResponse response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      System.out.println("statusCode=" + statusCode);
      HttpEntity entity = response.getEntity();
      String result = null;
      if (entity != null) {
        result = EntityUtils.toString(entity, defaultChartSet);
      }
      EntityUtils.consume(entity);
      response.close();
      return result;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      throw e;

    }

  }

  public static void main(String[] args) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("date", "2015-01-01");
    params.put("area", "0");
    params.put("weeks", "1");
    String result = HttpClientTool.doGet("http://localhost:5055/console/rest/ptconsole/getUserWeekPreserve", params);
    System.out.println(result);
  }
}
