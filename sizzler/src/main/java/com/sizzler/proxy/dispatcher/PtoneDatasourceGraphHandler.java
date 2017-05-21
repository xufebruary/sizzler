package com.sizzler.proxy.dispatcher;

import java.util.List;

public interface PtoneDatasourceGraphHandler<T extends PtoneDatasourceGraphDesc> {

  public List<PtoneVariableData> handle(T ptoneData);

}
