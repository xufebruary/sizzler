package com.sizzler.provider.common;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.extension.SPI;

// 当用户再次编辑excel、mysql的table时，调用该接口来取得数据填充的数据
@SPI
public interface EditorDataProvider {

  public EditorDataResponse getEditorData(EditorDataRequest request) throws ServiceException;

}
