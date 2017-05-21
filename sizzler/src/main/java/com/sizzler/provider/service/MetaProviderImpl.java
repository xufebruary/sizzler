package com.sizzler.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.provider.common.MetaProvider;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.MetaResponse;

@Service("metaProvider")
public class MetaProviderImpl implements MetaProvider {

  @Autowired
  private MetaProvider dataBaseProbider;

  @Override
  public MetaResponse getMeta(MetaRequest metaRequest) throws ServiceException {

//    MetaProvider metaProvider = ExtensionLoader.getExtensionLoader(MetaProvider.class)
//        .getAdaptiveExtension();
//    return metaProvider.getMeta(metaRequest);

    return dataBaseProbider.getMeta(metaRequest);
  }

}
