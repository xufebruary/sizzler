package com.sizzler.provider.service.database;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.impl.DefaultDataResponse;
import com.sizzler.provider.domain.request.DataBaseDataRequest;
import com.sizzler.provider.domain.request.DataBaseEditorDataRequest;
import com.sizzler.provider.domain.request.DataBaseFileMetaRequest;
import com.sizzler.provider.domain.request.DataBaseMetaFolderRequest;
import com.sizzler.provider.domain.response.DataBaseEditorDataResponse;
import com.sizzler.provider.domain.response.DataBaseFileMetaResponse;
import com.sizzler.provider.domain.response.DataBaseMetaFolderResponse;
import com.sizzler.provider.domain.response.DataBaseMetaResponse;


public interface DataBaseGatherService {

  public DataBaseMetaResponse getMeta(MetaRequest request) throws ServiceException;

  public DataBaseMetaFolderResponse getFolderMeta(DataBaseMetaFolderRequest request)
      throws ServiceException;

  public DataBaseEditorDataResponse getEditorData(DataBaseEditorDataRequest request)
      throws ServiceException;

  public DataBaseFileMetaResponse getPtoneFile(DataBaseFileMetaRequest request)
      throws ServiceException;

  public DefaultDataResponse getData(DataBaseDataRequest dataRequest) throws ServiceException;

}
