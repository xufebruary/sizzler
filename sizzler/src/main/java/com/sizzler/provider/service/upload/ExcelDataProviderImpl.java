package com.sizzler.provider.service.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.DataRequest;
import com.sizzler.provider.common.DataResponse;
import com.sizzler.provider.domain.request.ExcelDataRequest;

@Service("excelDataProvider")
public class ExcelDataProviderImpl implements DataProvider {

  @Autowired
  private UploadGatherService uploadGatherService;

  public UploadGatherService getUploadGatherService() {
    return uploadGatherService;
  }

  public void setUploadGatherService(UploadGatherService uploadGatherService) {
    this.uploadGatherService = uploadGatherService;
  }

  @Override
  public DataResponse getData(DataRequest dataRequest) {
    return uploadGatherService.getData((ExcelDataRequest) dataRequest);
  }

}
