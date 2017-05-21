package com.sizzler.provider.common;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.SPI;

/**
 * 用于取得数据源的配置信息的统一接口
 */
@SPI
public interface MetaProvider {
  
  public MetaResponse getMeta(MetaRequest metaRequest) throws ServiceException;
  
}
