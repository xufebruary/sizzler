package com.sizzler.provider.service.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.DataRequest;
import com.sizzler.provider.common.DataResponse;
import com.sizzler.provider.common.EditorDataProvider;
import com.sizzler.provider.common.EditorDataRequest;
import com.sizzler.provider.common.EditorDataResponse;
import com.sizzler.provider.common.MetaProvider;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.domain.request.DataBaseDataRequest;
import com.sizzler.provider.domain.request.DataBaseEditorDataRequest;
import com.sizzler.provider.domain.request.DataBaseFileMetaRequest;
import com.sizzler.provider.domain.request.DataBaseMetaFolderRequest;

@Service("dataBaseProbider")
public class DataBaseProviderImpl implements DataProvider, MetaProvider, EditorDataProvider {
  
  private static final Logger log = LoggerFactory.getLogger(DataBaseProviderImpl.class);

  @Autowired
  private DataBaseGatherService dataBaseGatherService;

  @Override
  public DataResponse getData(DataRequest dataRequest) {
    long start = System.currentTimeMillis();
    DataResponse dataResponse = dataBaseGatherService.getData((DataBaseDataRequest) dataRequest);
    long end = System.currentTimeMillis();
    log.info("execute-query:" + dataRequest.getQueryRequest().getQuery() + " ->" + (end - start));
    return dataResponse;
  }

  @Override
  public EditorDataResponse getEditorData(EditorDataRequest request) {
    return dataBaseGatherService.getEditorData((DataBaseEditorDataRequest) request);
  }

  @Override
  public MetaResponse getMeta(MetaRequest metaRequest) throws ServiceException {
    MetaResponse metaResponse = null;
    if (metaRequest instanceof DataBaseMetaFolderRequest) {
      metaResponse = dataBaseGatherService.getFolderMeta((DataBaseMetaFolderRequest) metaRequest);
    } else if (metaRequest instanceof DataBaseFileMetaRequest) {
      metaResponse = dataBaseGatherService.getPtoneFile((DataBaseFileMetaRequest) metaRequest);
    } else {
      metaResponse = dataBaseGatherService.getMeta(metaRequest);
    }
    return metaResponse;
  }

}
