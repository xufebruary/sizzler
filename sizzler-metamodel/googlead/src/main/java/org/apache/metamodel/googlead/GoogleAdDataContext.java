package org.apache.metamodel.googlead;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.metamodel.MetaModelException;
import org.apache.metamodel.googlead.model.AdWordsAPILocation;
import org.apache.metamodel.googlead.util.AdWordsMap;
import org.apache.metamodel.googlead.util.AdWordsSessionFactory;
import org.apache.metamodel.googlead.util.AdwordsReportType;
import org.apache.metamodel.ptutil.StringUtil;
import org.apache.metamodel.saas.SaasServiceDataContext;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.util.CommonQueryRequest;
import org.apache.metamodel.util.Oauth2Token;
import org.apache.metamodel.util.SimpleTableDef;

import au.com.bytecode.opencsv.CSVReader;

import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.jaxb.v201702.DownloadFormat;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.v201702.ReportDownloader;
import com.ptmind.ptone.metamodel.pojo.ArrayTableDataProvider;

/**
 * Created by ptmind on 2015/12/3.
 */
public class GoogleAdDataContext extends SaasServiceDataContext {

  public static String defaultSchemaName = "googlead";


  private Oauth2Token oauth2Token;
  private String developToken;
  private String customterId;
  private String userAgent;

  private AdwordsReportType reportType;


  public GoogleAdDataContext(Oauth2Token oauth2Token, String developToken, String customterId,
      CommonQueryRequest queryRequest, String userAgent, AdwordsReportType reportType) {
    super(queryRequest);
    this.oauth2Token = oauth2Token;
    this.developToken = developToken;
    this.customterId = customterId;
    this.userAgent = userAgent;
    this.reportType = reportType;

    // 修正重复维度
    String dimensions = this.queryRequest.getDimensions();
    if (StringUtil.isNotBlank(dimensions)) {
      String[] dimensionArray = dimensions.split(",");
      List<String> dimensionList = new ArrayList<String>(); // 无重复维度列表
      for (String d : Arrays.asList(dimensionArray)) {
        if (!dimensionList.contains(d)) {
          dimensionList.add(d);
        }
      }
      this.queryRequest.setDimensions(StringUtil.join(dimensionList, ","));
    }

    // 修正重复指标
    String metrics = this.queryRequest.getMetrics();
    if (StringUtil.isNotBlank(metrics)) {
      String[] metricsArray = metrics.split(",");
      List<String> metricsList = new ArrayList<String>(); // 无重复维度列表
      for (String m : Arrays.asList(metricsArray)) {
        if (!metricsList.contains(m)) {
          metricsList.add(m);
        }
      }
      this.queryRequest.setMetrics(StringUtil.join(metricsList, ","));
    }

  }


  @Override
  protected void initTableDataProvider(SimpleTableDef tableDef) {
    try {
      AdWordsSession adWordsSession =
          AdWordsSessionFactory.createAdWordsSession(getOauth2Token(), getDevelopToken(),
              getUserAgent());
      adWordsSession.setClientCustomerId(customterId);
      String query = buildQuery();
      System.out.println("query:" + query);
      ReportingConfiguration reportingConfiguration = createReportingConfiguration();
      adWordsSession.setReportingConfiguration(reportingConfiguration);

      ReportDownloadResponse response =
          new ReportDownloader(adWordsSession).downloadReport(query, DownloadFormat.CSV);
      String resultData = response.getAsString();
      convertResultDataToTableDataProvider(tableDef, resultData);

    } catch (Exception e) {
      throw new MetaModelException(e);
    }


  }

  private String buildQuery() {
    String dimensions = queryRequest.getDimensions();
    String metrics = queryRequest.getMetrics();
    String filters = queryRequest.getFilters();
    StringBuilder queryBuilder = new StringBuilder("");
    String fixEndSelect = "";

    queryBuilder.append("select  ");
    // select dimensions,metrics
    if (StringUtil.isNotBlank(dimensions)) {
      queryBuilder.append(dimensions).append(",");
      String[] dimensionArray = dimensions.split(",");
      List<String> dimensionList = Arrays.asList(dimensionArray);;

      // RegionCriteriaId,MetroCriteriaId,CityCriteriaId,MostSpecificCriteriaId的查询必须带着CountryCriteriaId
      if (dimensionList.contains("RegionCriteriaId") || dimensionList.contains("MetroCriteriaId")
          || dimensionList.contains("CityCriteriaId")
          || dimensionList.contains("MostSpecificCriteriaId")) {
        if (!dimensionList.contains("CountryCriteriaId")) {
          fixEndSelect = ",CountryCriteriaId";
        }
      }
    }

    queryBuilder.append(metrics).append(fixEndSelect).append(" from ")
        .append(reportType.toString());

    if (filters != null && !"".equals(filters)) {
      queryBuilder.append("  where  ").append(filters);
    }

    String startDate = queryRequest.getStartDate();
    String endDate = queryRequest.getEndDate();

    if (startDate != null && !"".equals(startDate)) {
      queryBuilder.append(" during ").append(startDate).append(",").append(endDate);
    }

    return queryBuilder.toString();
  }

  public static ReportingConfiguration createReportingConfiguration() {
    ReportingConfiguration reportingConfiguration =
        new ReportingConfiguration.Builder().skipReportHeader(true).skipColumnHeader(false)
            .skipReportSummary(false)
            // Set to false to exclude rows with zero impressions.
            // 某些report设置为true时，查询报错，比如 GEO_PERFORMANCE_REPORT
            .includeZeroImpressions(false).build();

    return reportingConfiguration;
  }

  private void convertResultDataToTableDataProvider(SimpleTableDef tableDef, String resultData) {
    // 返回的dataStr的格式为：
    // Header \n row \n Total
    // Account,Cost\nptone,0\nTotal,0

    // String[] dataRow=resultData.split("\\n");

    // 数值中包含逗号分隔的格式化数据（如： 2016-03-01,"2,492.0" ）
    List<String[]> dataRowList = new ArrayList<>();
    CSVReader csvReader = null;
    InputStream inputStream = null;
    try {
      inputStream = new ByteArrayInputStream(resultData.getBytes());
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      csvReader = new CSVReader(reader);
      dataRowList = csvReader.readAll();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
        if (csvReader != null) {
          csvReader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    List<Object[]> rowList = new ArrayList<>();
    ColumnType[] columnTypes = tableDef.getColumnTypes();
    String[] columnNames = tableDef.getColumnNames();
    for (int i = 1; i < dataRowList.size() - 1; i++) {
      // Object[] row = dataRow[i].split(",");
      Object[] row = dataRowList.get(i);
      for (int j = 0; j < columnTypes.length; j++) {
        Object valueObj = row[j];

        if (ColumnType.PERCENT.equals(columnTypes[j])) {
          String valueStr = (valueObj != null ? String.valueOf(valueObj) : "0");
          row[j] = valueStr.replaceAll("%", "");
        } else if (ColumnType.CURRENCY.equals(columnTypes[j])) {
          String valueStr = valueObj != null ? String.valueOf(valueObj).replace(",", "") : "0"; // 去除逗号
          Double value = (valueStr.isEmpty() ? 0 : Double.valueOf(valueStr));
          value = value / 1000000; // 修正adwords的money类型数据（需要除以1000000）
          row[j] = String.valueOf(value);
        } else if (ColumnType.NUMBER.equals(columnTypes[j])
            || ColumnType.DECIMAL.equals(columnTypes[j]) || ColumnType.LONG.equals(columnTypes[j])
            || ColumnType.INTEGER.equals(columnTypes[j])
            || ColumnType.DOUBLE.equals(columnTypes[j]) || ColumnType.FLOAT.equals(columnTypes[j])) {
          String valueStr = valueObj != null ? String.valueOf(valueObj).replace(",", "") : "0"; // 去除逗号
          row[j] = String.valueOf(valueStr);
        }

        // 对于某些列需要进行特殊处理，比如 CountryId需要转换为CountryName
        String columnName = columnNames[j];
        if (columnName.equalsIgnoreCase("CountryCriteriaId")
            || columnName.equalsIgnoreCase("RegionCriteriaId")
            || columnName.equalsIgnoreCase("MetroCriteriaId")
            || columnName.equalsIgnoreCase("CityCriteriaId")) {
          AdWordsAPILocation adWordsAPILocation = AdWordsMap.findById(valueObj.toString());
          if (adWordsAPILocation != null) {
            row[j] = adWordsAPILocation.getName();
          } else {
            System.out.println(valueObj.toString());
          }

        }
      }
      rowList.add(row);
    }
    tableDataProvider = new ArrayTableDataProvider(tableDef, rowList);
  }


  @Override
  protected String getMainSchemaName() throws MetaModelException {
    if (getSchemaName() == null) {
      return defaultSchemaName;
    }
    return getSchemaName();
  }


  public String getTableName() {
    if (tableName == null) {
      return reportType.toString();
    }
    return tableName;
  }



  public Oauth2Token getOauth2Token() {
    return oauth2Token;
  }

  public void setOauth2Token(Oauth2Token oauth2Token) {
    this.oauth2Token = oauth2Token;
  }

  public String getDevelopToken() {
    return developToken;
  }

  public void setDevelopToken(String developToken) {
    this.developToken = developToken;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public AdwordsReportType getReportType() {
    return reportType;
  }

  public void setReportType(AdwordsReportType reportType) {
    this.reportType = reportType;
  }

  public String getCustomterId() {
    return customterId;
  }

  public void setCustomterId(String customterId) {
    this.customterId = customterId;
  }
}
