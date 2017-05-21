package com.sizzler.provider.common;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.SPI;

@SPI
public interface UpdateDataProvider {
  public UpdateDataResponse updateData(UpdateDataRequest request) throws ServiceException;
}
