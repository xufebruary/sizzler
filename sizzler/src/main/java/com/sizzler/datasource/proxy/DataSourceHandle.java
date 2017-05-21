package com.sizzler.datasource.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.SpringContextUtil;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.dto.UIEditorData;
import com.sizzler.provider.common.UpdateDataResponse;
import com.sizzler.system.ServiceFactory;

public abstract class DataSourceHandle {

  protected ServiceFactory serviceFactory;
  protected DataSourceBuild dataSourceBuild;
  protected Logger logger = LoggerFactory.getLogger(DataSourceHandle.class);

  public DataSourceHandle() {
    this.serviceFactory = SpringContextUtil.getBean("serviceFactory");
    this.dataSourceBuild = SpringContextUtil.getBean("dataSourceBuild");
  }

  public abstract String getAccountSchema(UserConnection userConnection, String folderId);

  public abstract UserConnectionSourceVo pullRemoteData(UserConnection userConnection, String folderId,
      String fileId);

  /**
   * 原参数结构：UserConnection userConnection, UserConnectionSource userConnectionSource, UserConnectionSourceVo
   * sourceVo, Map<String, Object> schemaMap, Boolean isAutoUpdate<br>
   * 原方法名：saveFileToHDFS<br>
   * 用于存储文件数据到本地数据库中
   * 
   * @author li.zhang
   * @modify you.zou
   * @date 2016年11月21日 上午10:41:47
   * @param userConnection
   * @param sourceVo
   * @param sourceDto
   * @param isAutoUpdate
   * @return
   * @throws Exception
   */
  public UpdateDataResponse saveFileDataToDB(UserConnection userConnection,
      UserConnectionSourceVo sourceVo, UserConnectionSourceDto sourceDto, Boolean isAutoUpdate)
      throws Exception {
    return null;
  };

  /**
   * 增加IsUpdate参数，标示本次操作是否是更新操作
   * @author li.zhang
   * @modify you.zou
   * @date 2016年11月21日 下午3:33:39
   * @param userConnection
   * @param sourceDto
   * @param isAutoUpdate
   * @param isUpdate
   * @return
   * @throws Exception
   */
  public abstract UIEditorData getEditData(UserConnection userConnection,
      UserConnectionSourceDto sourceDto, Boolean isAutoUpdate, Boolean isUpdate) throws Exception;
}
