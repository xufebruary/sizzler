package com.sizzler.provider.adaptive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.common.extension.PtoneAdaptive;
import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.DataRequest;
import com.sizzler.provider.common.DataResponse;

@PtoneAdaptive
public class DataProviderAdaptive implements DataProvider {

  private static final Logger log = LoggerFactory.getLogger(DataProviderAdaptive.class);

  @Override
  public DataResponse getData(DataRequest dataRequest) throws ServiceException {

    log.info("execute DataProviderAdaptive getData");

    DataProvider dataProvider = null;
    ExtensionLoader<DataProvider> extensionLoader = ExtensionLoader
        .getExtensionLoader(DataProvider.class);
    String dsCode = dataRequest.getUserConnection().getDsCode();
    if (dsCode != null && dsCode.length() > 0) {
      dataProvider = extensionLoader.getExtension(dsCode);
    }

    if (dataProvider == null) {
      throw new IllegalStateException("Can't init dsCode=" + dsCode + "'s DataProvider ");
    }

    return dataProvider.getData(dataRequest);
  }

}
