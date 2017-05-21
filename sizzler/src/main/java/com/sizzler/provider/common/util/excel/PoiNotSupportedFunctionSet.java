package com.sizzler.provider.common.util.excel;

import org.apache.poi.ss.formula.WorkbookEvaluator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ptmind on 2016/1/18.
 */
public class PoiNotSupportedFunctionSet {

  private static Set<String> poiNotSupportedFunctionSet = new HashSet<>();

  static {
    for (String unsupportedFunName : WorkbookEvaluator.getNotSupportedFunctionNames()) {
      poiNotSupportedFunctionSet.add(unsupportedFunName);
    }
  }

  public static boolean isPoiNotSupportedFunction(String functionName) {
    if (functionName == null || functionName.equals("")) {
      return false;
    }
    return poiNotSupportedFunctionSet.contains(functionName.toUpperCase());
  }

}
