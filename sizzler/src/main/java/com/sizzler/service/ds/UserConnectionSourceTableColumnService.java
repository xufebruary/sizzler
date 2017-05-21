package com.sizzler.service.ds;

import java.util.List;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableColumnDto;

/**
 * 
 * @date: 2016年11月1日
 * @author peng.xu
 */
public interface UserConnectionSourceTableColumnService extends
    ServiceBaseInterface<UserConnectionSourceTableColumn, String> {
  
  /**
   * 获取用户有效列（非忽略的列）
   * @param colId
   * @return
   * @date: 2016年11月18日
   * @author peng.xu
   */
  public UserConnectionSourceTableColumn getAvailableColumn(String colId);


  /**
   * 获取source下所有列
   * @param sourceId
   * @return
   * @date: 2016年11月18日
   * @author peng.xu
   */
  public List<UserConnectionSourceTableColumn> findBySourceId(String sourceId);

  /**
   * 获取table下所有列
   * @param tableId
   * @return
   * @date: 2016年11月18日
   * @author peng.xu
   */
  public List<UserConnectionSourceTableColumn> findByTableId(String tableId);
  
  /**
   * 获取table下所有列的Dto对象
   * @param tableId
   * @return
   * @date: 2016年11月18日
   * @author peng.xu
   */
  public List<UserConnectionSourceTableColumnDto> findByTableIdOfDto(String tableId);

  /**
   * 获取table下对应类型的所有列
   * 
   * @param tableId
   * @param typeArray: metrics || dimension || ignore
   * @return
   * @date: 2016年11月18日
   * @author peng.xu
   */
  public List<UserConnectionSourceTableColumn> findByTableId(String tableId, String[] typeArray);
  /**
   * 获取table下对应类型的所有列的Dto对象
   * @author you.zou
   * @date 2016年11月18日 下午4:40:24
   * @param tableId
   * @param typeArray
   * @return
   */
  public List<UserConnectionSourceTableColumnDto> findByTableIdOfDto(String tableId, String[] typeArray);
  
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

}
