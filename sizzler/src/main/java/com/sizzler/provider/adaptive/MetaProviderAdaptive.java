package com.sizzler.provider.adaptive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.common.extension.PtoneAdaptive;
import com.sizzler.provider.common.MetaProvider;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.MetaResponse;

@PtoneAdaptive
public class MetaProviderAdaptive implements MetaProvider {
  private static final Logger log = LoggerFactory.getLogger(MetaProviderAdaptive.class);

  @Override
  public MetaResponse getMeta(MetaRequest metaRequest) {
    log.info("execute MetaProviderAdaptive getMeta");
    MetaProvider metaProvider = null;
    ExtensionLoader<MetaProvider> metaProviderExtensionLoader = ExtensionLoader
        .getExtensionLoader(MetaProvider.class);
    String dsCode = metaRequest.getUserConnection().getDsCode();
    if (dsCode != null && dsCode.length() > 0) {
      metaProvider = metaProviderExtensionLoader.getExtension(dsCode);
    }
    if (metaProvider == null) {
      throw new IllegalStateException("Can't init dsCode=" + dsCode + "'s MetaProvider ");
    }
    return metaProvider.getMeta(metaRequest);
  }
}
