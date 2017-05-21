package com.sizzler.provider.common.util;


import com.ptmind.common.utils.StringUtil;

/**
 * Created by ptmind on 2015/10/22.
 */
public class CommonPtoneDataToPtoneResponseDataConverter extends
    PtoneDataToPtoneResponseDataConverter {

  @Override
  public Object convertStringToDataType(String value, String dataType) {
    // return null;
    return StringUtil.convertStringToDataType(value, dataType);
  }
}
