package com.sizzler.provider.common.util;


import com.sizzler.common.utils.StringUtil;

public class CommonPtoneDataToPtoneResponseDataConverter extends
    PtoneDataToPtoneResponseDataConverter {

  @Override
  public Object convertStringToDataType(String value, String dataType) {
    // return null;
    return StringUtil.convertStringToDataType(value, dataType);
  }
}
