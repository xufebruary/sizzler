package com.sizzler.provider.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.metamodel.util.CommonQueryRequest;

/**
 * Created by ptmind on 2015/10/23.
 */
public class GatherUtil {
  public static List<List<String>> sortRows(List<List<String>> rows) {
    List<List<String>> sortResult = new ArrayList<>();
    List<PtoneDataRow> dataRowList = new ArrayList<>();
    for (List<String> row : rows) {
      PtoneDataRow dataRow = new PtoneDataRow();
      dataRow.setDate(row.get(0));
      dataRow.setRow(row);
      dataRowList.add(dataRow);
    }

    Collections.sort(dataRowList);
    for (PtoneDataRow ptoneDataRow : dataRowList) {
      sortResult.add(ptoneDataRow.getRow());
    }

    return sortResult;
  }

  public static boolean hasDateDimension(CommonQueryRequest queryRequest) {
    String dimensions = queryRequest.getDimensions();
    if (dimensions == null || dimensions.trim().equals("")) {
      return false;
    } else {
      return dimensions.toLowerCase().matches(".*(date|week|month|year).*");
    }
  }

}
