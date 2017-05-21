package com.sizzler.proxy.dispatcher;

import java.util.List;


public interface PtoneGraphVariableDataHandler<T extends PtoneGraphVariableDataDesc> {

  public List<PtoneVariableData> handle(T ptoneData);

}
