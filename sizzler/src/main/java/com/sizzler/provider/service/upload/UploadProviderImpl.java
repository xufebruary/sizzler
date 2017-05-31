package com.sizzler.provider.service.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.provider.common.EditorDataProvider;
import com.sizzler.provider.common.EditorDataRequest;
import com.sizzler.provider.common.EditorDataResponse;
import com.sizzler.provider.common.UpdateDataProvider;
import com.sizzler.provider.common.UpdateDataRequest;
import com.sizzler.provider.common.UpdateDataResponse;
import com.sizzler.provider.common.impl.ExcelEditorDataRequest;
import com.sizzler.provider.common.impl.ExcelUpdateDataRequest;

@Service("uploadProvider")
public class UploadProviderImpl implements UpdateDataProvider, EditorDataProvider {

  @Autowired
  private UploadGatherService uploadGatherService;

  public UploadGatherService getUploadGatherService() {
    return uploadGatherService;
  }

  public void setUploadGatherService(UploadGatherService uploadGatherService) {
    this.uploadGatherService = uploadGatherService;
  }

  @Override
  public UpdateDataResponse updateData(UpdateDataRequest request) throws ServiceException {
    return uploadGatherService.updateData((ExcelUpdateDataRequest) request);
  }

  @Override
  public EditorDataResponse getEditorData(EditorDataRequest request) throws ServiceException {
    return uploadGatherService.getEditorData((ExcelEditorDataRequest) request);
  }

}
