package com.sizzler.provider.service.upload;

import org.springframework.beans.factory.annotation.Autowired;

import com.sizzler.provider.common.MetaProvider;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.common.impl.DefaultMetaRequest;
import com.sizzler.provider.domain.request.UploadFileMetaRequest;

public class UploadMetaProviderImpl implements MetaProvider {

  @Autowired
  private UploadGatherService uploadGatherService;

  @Override
  public MetaResponse getMeta(MetaRequest metaRequest) {
    if (metaRequest instanceof UploadFileMetaRequest) {
      return uploadGatherService.getMeta(metaRequest);
    } else if (metaRequest instanceof DefaultMetaRequest) {
      return uploadGatherService.getMeta(metaRequest);
    }
    return null;
  }
}
