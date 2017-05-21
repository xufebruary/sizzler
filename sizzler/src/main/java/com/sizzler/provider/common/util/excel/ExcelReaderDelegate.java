package com.sizzler.provider.common.util.excel;

import java.io.InputStream;
import java.util.Map;

public interface ExcelReaderDelegate {
  public Map<String, Object> createSchema(String fileName, InputStream inputStream,
      boolean maxRowLimit) throws Exception;

}
