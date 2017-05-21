package com.sizzler.service;

import java.util.List;

import org.apache.metamodel.schema.MutableSchema;

import com.sizzler.common.exception.BusinessException;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.dto.DsContentView;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UiAccountConnection;
import com.sizzler.domain.ds.dto.UserAccountSource;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.provider.common.UpdateDataResponse;

public interface DataSourceManagerService {

  /**
   * 从本地文件中保存或更新数据
   * @param acceptTable
   * @return
   * @throws Exception
   */
  /*
   * public UIAcceptTable saveOrUpdateEditorDataOfLocalFile(UIAcceptTable acceptTable) throws
   * Exception;
   */

  public abstract UserConnectionSourceVo saveOrUpdateEditorDataToFile(UserConnectionSourceVo acceptTable)
      throws BusinessException;

  /**
   * 更新远端文件的数据
   */
  public abstract UserConnectionSourceVo updateRemoteSourceData(UserConnectionSource source)
      throws BusinessException;

  /**
   * 更新远端文件的数据
   * @param innerFunction 是否内部调用
   * @param isAutoUpdate 是否自动更新
   * @param isUpdate 是否是更新时调用
   */
  public abstract UserConnectionSourceVo getEditorDataByConnectionId(String connectionId, String sourceId,
      boolean innerFunction,boolean isAutoUpdate, boolean isUpdate) throws BusinessException;

  public abstract void deleteConnectionInfo(UserConnection userConnection);

  public abstract String getDataSourceAccountSchema(String connectionId, String folderId,
      boolean refresh) throws BusinessException;

  public abstract UserConnectionSourceVo pullRemoteData(String connectionId, String folderId, String fileId)
      throws BusinessException;

  @Deprecated
  public abstract List<UserAccountSource> getAuthAccount(String connectionId, String uid,
      String dsId);

  public abstract List<PtoneMetricsDimension> getUserMetricsDimensionList(long dsId,
      String tableId, String[] typeArray);

  @Deprecated
  public abstract List<DsContentView> getUserDsContentView(String uid);

  public abstract void delSavedFile(String sourceId);

  public UserConnectionSource getUserConnectionSourceByTableId(String tableId);
  
  /**
   * 通过TableID获取SourceDto，包含TableID对应的Table对象，
   * @author you.zou
   * @date 2016年11月23日 下午5:53:45
   * @param tableId
   * @return
   */
  public UserConnectionSourceDto getSourceDtoByTableIdIncludeTable(String tableId);
  
  /**
   * 直接通过SourceDto对象转换成MutableSchema对象
   * @author you.zou
   * @date 2016年11月21日 下午12:05:30
   * @param source
   * @return
   */
  public abstract MutableSchema sourceDtoToSchema(UserConnectionSourceDto source);

  /**
   * 根据TableID找到TableDto、SourceDto、ColumnDto，并将三个对象组装进MutableSchema
   * @author you.zou
   * @date 2016年11月23日 上午11:38:50
   * @param tableId
   * @return
   */
  public abstract MutableSchema getMutableSchemaByTableId(String tableId);

  public abstract UpdateDataResponse refreshFileFromRemote(UserConnectionSourceVo acceptTable)
      throws Exception;

  /*@Deprecated
  public abstract List<MetaContentNode> getWidgetAuthAccount(String connectionId, String uid,
      String dsId);*/

  public abstract List<MetaContentNode> getSpaceWidgetAuthAccount(String spaceId,
      String connectionId, String dsId);

  public abstract Long getAccountWidgetCount(String connectionId, String uid, String dsId);

  public abstract Long getSourceWidgetCount(String sourceId, String uid);

  public abstract void testRemoteConnection(UserConnection userConnection) throws ServiceException;

  public abstract void updateConnectionSourcePath(String path, String connectionId);

  public abstract List<UserConnectionSource> getConnectionSourceList(String connectionId);

  public abstract UserConnectionSource getConnectionSourceById(String sourceId);

  public abstract UserConnectionSource updateConnectionSource(UserConnectionSource connectionSource);

  public abstract List<DsContentView> getSpaceDsContentView(String spaceId,String userSource);

  public abstract List<UserAccountSource> getSpaceAuthAccount(String spaceId, String connectionId,
      String dsId);

  /**
   * @Description: 保存新增的授权账号信息的UserConnection，如果是同一人在同一空间下的授权则不新增记录
   * 
   * @param config UserConnection的config信息
   * @date: 2016年10月10日
   * @author peng.xu
   */
  public abstract JsonView addAccountConnection(UiAccountConnection uiAccountConnection, String sign);

}
