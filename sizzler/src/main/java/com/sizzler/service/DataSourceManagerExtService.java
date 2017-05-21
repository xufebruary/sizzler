package com.sizzler.service;

import java.util.Map;

import com.sizzler.domain.ds.vo.UserConnectionSourceVo;

/**
 * DataSourceManagerService扩展服务
 * @author you.zou
 * @date 2016年10月9日 下午6:40:31
 */
public interface DataSourceManagerExtService {

  /**
   * 新增的在执行saveOrUpdateEditorDataToFile方法前单独执行数据库操作的方法
   * @author you.zou
   * @date 2016年10月9日 下午3:30:29
   * @param acceptTable
   * @throws Exception
   * @return Map<String, Object>
   */
  public abstract Map<String, Object> beforeSaveOrUpdateEditorDataToFile(UserConnectionSourceVo acceptTable)
      throws Exception;
}
