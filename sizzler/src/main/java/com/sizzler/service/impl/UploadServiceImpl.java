package com.sizzler.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.impl.DefaultMetaRequest;
import com.sizzler.provider.domain.response.UploadFileMetaResponse;
import com.sizzler.service.UploadService;
import com.sizzler.system.ServiceFactory;

/**
 * Created by li.zhang on 2015/4/2.
 */
@Service("uploadService")
public class UploadServiceImpl implements UploadService {
  @Autowired
  private ServiceFactory serviceFactory;
  private static Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

  public UserConnectionSourceVo getSourceVo(UserConnection connection) {
    UserConnectionSourceVo sourceVo = null;
    LogMessage logMessage = new LogMessage(); 
    try{
      logMessage.setOperate("getSourceVo");
      logMessage.addOperateInfo("connectionId", connection.getConnectionId());
      MetaRequest metaRequest = new DefaultMetaRequest(connection);
      UploadFileMetaResponse response =
          (UploadFileMetaResponse) serviceFactory.getMetaProvider().getMeta(metaRequest);
      sourceVo = serviceFactory.getDataSourceBuild().buildSourceVoByPtoneFile(response.getFile(), connection);
    }catch(Exception e){
     LogMessageUtil.addErrorExceptionMessage(logMessage, e.getMessage());
     logger.error(e.getMessage(), e);
    }finally{
      logger.info(logMessage.toString());
    }
    return sourceVo;

  }
}
