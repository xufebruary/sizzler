package com.sizzler.provider.service;

import org.springframework.stereotype.Service;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.provider.common.UpdateDataProvider;
import com.sizzler.provider.common.UpdateDataRequest;
import com.sizzler.provider.common.UpdateDataResponse;

@Service("updateDataProvider")
public class UpdateDataProviderImpl implements UpdateDataProvider {

  @Override
  public UpdateDataResponse updateData(UpdateDataRequest request) throws ServiceException {
    UpdateDataProvider updateDataProvider = ExtensionLoader.getExtensionLoader(
        UpdateDataProvider.class).getAdaptiveExtension();
    return updateDataProvider.updateData(request);
  }

}
