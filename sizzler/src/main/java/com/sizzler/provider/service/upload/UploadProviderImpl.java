package com.sizzler.provider.service.upload;

import org.springframework.beans.factory.annotation.Autowired;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.provider.common.EditorDataProvider;
import com.sizzler.provider.common.EditorDataRequest;
import com.sizzler.provider.common.EditorDataResponse;
import com.sizzler.provider.common.UpdateDataProvider;
import com.sizzler.provider.common.UpdateDataRequest;
import com.sizzler.provider.common.UpdateDataResponse;
import com.sizzler.provider.common.impl.ExcelEditorDataRequest;
import com.sizzler.provider.common.impl.ExcelUpdateDataRequest;

public class UploadProviderImpl implements UpdateDataProvider, EditorDataProvider {

  @Autowired
  private UploadGatherService uploadGatherService;

  @Override
  public UpdateDataResponse updateData(UpdateDataRequest request) throws ServiceException {
    return uploadGatherService.updateData((ExcelUpdateDataRequest) request);
  }

  @Override
  public EditorDataResponse getEditorData(EditorDataRequest request) throws ServiceException {
    return uploadGatherService.getEditorData((ExcelEditorDataRequest) request);
  }

}
