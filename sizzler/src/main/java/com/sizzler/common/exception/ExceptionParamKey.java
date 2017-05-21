package com.sizzler.common.exception;

public class ExceptionParamKey {

  /** user Id */
  public final static String UID = "uid";

  /** accounts Id */
  public final static String ACCOUNTS_ID = "accountsId";

  /** Campaign Id */
  public final static String CAMPAIGN_ID = "campaignId";

  /** object Id */
  public final static String OBJECT_ID = "objectId";

  /** 对应与user_connection表中的connection_id 字段 */
  public final static String USER_CONNECTION_ID = "userConnectionId";

  /** 对应与user_connection表中的config字段 */
  public final static String USER_CONNECTION_CONFIG = "userConnectionConfig";

  /** 取数时的查询条件 */
  public final static String DS_QUERY = "dataSourceQuery";

  /** 查询SQL */
  public final static String DS_SQL = "sql";

  /** 查询总数的SQL */
  public final static String DS_TOTALSQL = "totalSql";

  /** UUID */
  public final static String UUID = "uuid";

  /** oauth2Token */
  public final static String OAUTH2TOKEN = "oauth2Token";

  public static final String QUERY_REQUEST = "queryRequest";
}
