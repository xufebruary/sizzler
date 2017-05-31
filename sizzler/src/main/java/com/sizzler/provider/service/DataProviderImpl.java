package com.sizzler.provider.service;

import org.springframework.stereotype.Service;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.DataRequest;
import com.sizzler.provider.common.DataResponse;

@Service("dataProvider")
public class DataProviderImpl implements DataProvider {

  @Override
  public DataResponse getData(DataRequest dataRequest) throws ServiceException {
    DataProvider dataProvider = ExtensionLoader.getExtensionLoader(DataProvider.class)
        .getAdaptiveExtension();
    return dataProvider.getData(dataRequest);
  }

}
