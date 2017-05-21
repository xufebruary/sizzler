package com.sizzler.provider.common;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.SPI;

/**
 * 数据查询的统一入口
 */
@SPI
public interface DataProvider {

  public DataResponse getData(DataRequest dataRequest) throws ServiceException;

}
