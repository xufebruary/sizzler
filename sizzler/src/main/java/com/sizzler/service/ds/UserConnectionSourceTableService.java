package com.sizzler.service.ds;

import java.util.List;
import java.util.Map;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.ds.UserConnectionSourceTable;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableDto;

/**
 * 
 * @date: 2016年11月1日
 * @author peng.xu
 */
public interface UserConnectionSourceTableService extends
    ServiceBaseInterface<UserConnectionSourceTable, String> {
  
  /**
   * 通过SourceID获得Dto实体对象列表
   * @author you.zou
   * @date 2016年11月18日 下午4:14:40
   * @param sourceId
   * @return
   */
  public List<UserConnectionSourceTableDto> findBySourceIdOfDto(String sourceId);
  
  /**
   * 通过SourceID获得Dto实体对象列表，并且获取到table下的column列表
   * @author you.zou
   * @date 2016年11月18日 下午4:14:40
   * @param sourceId
   * @return
   */
  public List<UserConnectionSourceTableDto> findBySourceIdOfDtoIncludeColumns(String sourceId);
  
  /**
   * 通过SourceID获得实体对象列表
   * @author you.zou
   * @date 2016年11月18日 下午4:14:56
   * @param sourceId
   * @return
   */
  public List<UserConnectionSourceTable> findBySourceId(String sourceId);
  
  /**
   * 根据TableID找到包含Column列表的TableDto对象
   * @author you.zou
   * @date 2016年11月23日 上午10:54:54
   * @param tableId
   * @return
   */
  public UserConnectionSourceTableDto getByTableIdOfDtoIncludeClumns(String tableId);
  
  /**
   * 根据TableID找到TableDto对象
   * @author you.zou
   * @date 2016年11月23日 下午5:55:38
   * @param tableId
   * @return
   */
  public UserConnectionSourceTableDto getByTableIdOfDto(String tableId);
  
  /**
   * 根据sourceId修改状态
   * @author you.zou
   * @date 2016年11月30日 下午5:10:25
   * @param sourceId
   */
  public void updateStatusBySourceId(String status, String sourceId);
  
  /**
   * 根据connectionId修改状态
   * @author you.zou
   * @date 2016年11月30日 下午5:10:25
   * @param sourceId
   */
  public void updateStatusByConnectionId(String status, String connectionId);
  
  /**
   * 根据SourceID按照排序参数排序返回结果
   * @author you.zou
   * @date 2016年12月10日 下午2:35:23
   * @param sourceId
   * @param orderMap
   * @return
   */
  public List<UserConnectionSourceTable> findBySourceId(String sourceId, Map<String, String> orderMap);
  
}
