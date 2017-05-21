package com.sizzler.service.ds;

import java.util.List;
import java.util.Map;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.dto.UserAccountSource;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;

public interface UserConnectionSourceService extends
    ServiceBaseInterface<UserConnectionSource, String> {

  public abstract UserConnectionSource getSource(String sourceId);
  
  /**
   * 通过SourceID获取SourceDto对象，并且内部带有table、column列表
   * @author you.zou
   * @date 2016年11月18日 下午4:20:00
   * @param sourceId
   * @return
   */
  public abstract UserConnectionSourceDto getSourceDtoIncludeTables(String sourceId);
  
  /**
   * 通过SourceID获取SourceDto对象，包含Table信息，但不包含Column信息
   * @author you.zou
   * @date 2016年11月23日 下午4:55:35
   * @param sourceId
   * @return
   */
  public abstract UserConnectionSourceDto getSourceDtoIncludeTablesOutcludeColumns(String sourceId);
  /**
   * 通过SourceID获取SourceDto对象，但不包含Table信息
   * @author you.zou
   * @date 2016年11月18日 下午6:08:46
   * @param sourceId
   * @return
   */
  public abstract UserConnectionSourceDto getSourceDto(String sourceId);
  
  /**
   * 根据条件查询到SourceDto列表，并且每个SourceDto都包含Table信息
   * @author you.zou
   * @date 2016年11月21日 上午11:53:41
   * @param paramMap
   * @return
   */
  public abstract List<UserConnectionSourceDto> findSourceDtoByWhereIncludeTables(Map<String, Object[]> paramMap);
  
  /**
   * 根据条件查询到SourceDto列表，并且每个SourceDto都包含Table信息<br>
   * 支持排序，不包含columns信息
   * @author you.zou
   * @date 2016年11月23日 下午3:38:33
   * @param paramMap
   * @param orderMap
   * @return
   */
  public abstract List<UserConnectionSourceDto> findSourceDtoByWhereIncludeTablesOutcludeColumns(Map<String, Object[]> paramMap, Map<String, String> orderMap);

  public abstract void updateLastModifiedDate(String sourceId, long lastModifiedDate);

  public abstract void updateUpdateTime(String sourceId, long updateTime);

  public abstract List<UserAccountSource> getUserAccountSource(String uid, String dsId);

  public abstract void updateConnectionSourceRemoteStatus(String status, String fileId);

  // public abstract void updateConnectionSourceConfig(Map<String, Object> rowSumMap,String
  // fileId,String configName);
  public abstract void updateConnectionSourcePath(String path, String connectionId);

  public abstract void updateConnectionSourceConfig(String config, String sourceId);

  /**
   * 根据链接ID修改ConnectionSource的状态
   * @author you.zou
   * @date 2016年11月28日 上午10:04:12
   * @param status
   * @param connectionId
   */
  public abstract void updateConnectionSourceStatusByConnectionId(String status, String connectionId);
  
  public abstract void updateConnectionSourceRemoteStatusBySourceId(String sourceId, String status);

  // public abstract boolean appendSheetSchema(MutableTable[] mutableTables,UserConnection
  // userConnection,String fileId,String dsCode);
  // public abstract boolean delSheetSchema(MutableTable[] mutableTables, String fileId);
  public abstract List<UserAccountSource> getSpaceAccountSource(String spaceId, String dsId);

}
