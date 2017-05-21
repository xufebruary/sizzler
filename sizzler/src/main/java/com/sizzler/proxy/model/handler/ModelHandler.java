package com.sizzler.proxy.model.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.dto.SegmentData;
import com.sizzler.proxy.common.CommonHandler;
import com.sizzler.proxy.dispatcher.PtoneDatasourceDesc;
import com.sizzler.proxy.dispatcher.PtoneDatasourceHandler;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.proxy.model.ModelDataUtil;
import com.sizzler.service.data.ModelDataService;

@Component("modelHandler")
public class ModelHandler extends CommonHandler implements
    PtoneDatasourceHandler<PtoneDatasourceDesc> {

  private Logger log = LoggerFactory.getLogger(ModelHandler.class);

  @Autowired
  private ModelDataService modelDataService;

  @Autowired
  private ModelDataUtil modelDataUtil;

  @Override
  public PtoneWidgetParam checkAuth(UserConnection userConnection, PtoneWidgetParam ptoneWidgetParam) {
    String dsCode = ptoneWidgetParam.getDsCode();
    // 判断是否授权
    if (userConnection == null) {
      String message = "Not find PtoneUserConnection Auth, Please grant auth to ptone first !";
      ServiceException se = new ServiceException(message);
      if (DataBaseConfig.isDatabase(dsCode)) {
        se.setErrorCode(ErrorCode.CODE_NO_ACCOUNT_AUTH);
        se.setErrorMsg(ErrorCode.MSG_NO_ACCOUNT_AUTH);
      } else {
        se.setErrorCode(ErrorCode.CODE_FAILED);
        se.setErrorMsg(ErrorCode.MSG_FAILED);
      }
      throw se;
    } else {
      ptoneWidgetParam.setUserConnection(userConnection);
    }
    return ptoneWidgetParam;
  }

  @Override
  public List<PtoneVariableData> getTmpPtoneVariableDataList(PtoneWidgetInfo ptoneWidgetInfo,
      GaWidgetInfo gaWidgetInfo, PtoneVariableInfo ptoneVariableInfo,
      PtoneWidgetParam ptoneWidgetParam, Map<String, String> webParamMap) {
    return modelDataService.getData(ptoneWidgetInfo, gaWidgetInfo, ptoneVariableInfo,
        ptoneWidgetParam, webParamMap);
  }

  @Override
  public List<PtoneVariableData> handle(PtoneDatasourceDesc ptoneDatasourceDesc) {
    return super.commonHandle(ptoneDatasourceDesc, log);
  }

  @Override
  protected String parseSegments(SegmentData segments, PtoneDatasourceDesc ptoneDatasourceDesc) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String parseFilters(SegmentData filters, PtoneDatasourceDesc ptoneDatasourceDesc) {
    PtoneWidgetParam ptoneWidgetParam = ptoneDatasourceDesc.getPtoneWidgetParam();
    String userTz = ptoneWidgetParam.getTimeZone();
    String dsCode = ptoneWidgetParam.getDsCode();
    return modelDataUtil.parseFilter(filters, dsCode, userTz);
  }
}
