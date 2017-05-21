package com.sizzler.provider.service.upload;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.impl.DefaultDataResponse;
import com.sizzler.provider.common.impl.ExcelEditorDataRequest;
import com.sizzler.provider.common.impl.ExcelEditorDataResponse;
import com.sizzler.provider.common.impl.ExcelUpdateDataRequest;
import com.sizzler.provider.domain.request.ExcelDataRequest;
import com.sizzler.provider.domain.response.UploadFileMetaResponse;
import com.sizzler.provider.domain.response.UploadUpdateDataResponse;

public interface UploadGatherService {

  public UploadFileMetaResponse getMeta(MetaRequest metaRequest) throws ServiceException;

  public DefaultDataResponse getData(ExcelDataRequest dataRequest) throws ServiceException;

  public UploadUpdateDataResponse updateData(ExcelUpdateDataRequest request)
      throws ServiceException;

  public ExcelEditorDataResponse getEditorData(ExcelEditorDataRequest request)
      throws ServiceException;

}
