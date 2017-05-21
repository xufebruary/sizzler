package com.sizzler.provider.service.upload;

import org.springframework.beans.factory.annotation.Autowired;

import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.DataRequest;
import com.sizzler.provider.common.DataResponse;
import com.sizzler.provider.domain.request.ExcelDataRequest;

public class ExcelDataProviderImpl implements DataProvider {

  @Autowired
  private UploadGatherService uploadGatherService;

  @Override
  public DataResponse getData(DataRequest dataRequest) {
    return uploadGatherService.getData((ExcelDataRequest) dataRequest);
  }
  
}
