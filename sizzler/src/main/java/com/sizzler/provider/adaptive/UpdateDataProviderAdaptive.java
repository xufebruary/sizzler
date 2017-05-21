package com.sizzler.provider.adaptive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.common.extension.PtoneAdaptive;
import com.sizzler.provider.common.UpdateDataProvider;
import com.sizzler.provider.common.UpdateDataRequest;
import com.sizzler.provider.common.UpdateDataResponse;

@PtoneAdaptive
public class UpdateDataProviderAdaptive implements UpdateDataProvider {

  private static final Logger log = LoggerFactory.getLogger(UpdateDataProviderAdaptive.class);

  @Override
  public UpdateDataResponse updateData(UpdateDataRequest request) throws ServiceException {
    log.info("execute UpdateDataProviderAdaptive updateData");
    UpdateDataProvider updateDataProvider = null;
    ExtensionLoader<UpdateDataProvider> extensionLoader = ExtensionLoader
        .getExtensionLoader(UpdateDataProvider.class);
    String dsCode = request.getUserConnection().getDsCode();
    if (dsCode != null && dsCode.length() > 0) {
      updateDataProvider = extensionLoader.getExtension(dsCode);
    }
    if (updateDataProvider == null) {
      throw new IllegalStateException("Can't init dsCode=" + dsCode + "'s UpdateDataProvider ");
    }
    return updateDataProvider.updateData(request);
  }

}
