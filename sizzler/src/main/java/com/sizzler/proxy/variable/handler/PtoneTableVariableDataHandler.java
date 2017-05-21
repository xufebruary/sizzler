package com.sizzler.proxy.variable.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.proxy.dispatcher.PtoneGraphVariableDataHandler;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.variable.PtoneGraphVariableDataDescDispatcher;
import com.sizzler.proxy.variable.model.GraphVariableDataDesc;

@Component
public class PtoneTableVariableDataHandler implements
    PtoneGraphVariableDataHandler<GraphVariableDataDesc> {

  @Autowired
  private PtoneGraphVariableDataDescDispatcher dispatcher;

  public List<PtoneVariableData> handle(GraphVariableDataDesc graphDesc) {
    List<PtoneVariableData> ptoneVariableDataList =
        dispatcher.parseTableDataTable(graphDesc, graphDesc.getQueryParam());
    return ptoneVariableDataList;
  }

}
