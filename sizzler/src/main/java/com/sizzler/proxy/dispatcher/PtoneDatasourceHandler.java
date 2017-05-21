package com.sizzler.proxy.dispatcher;

import java.util.List;

public interface PtoneDatasourceHandler<T extends PtoneDatasourceDesc> {

  public List<PtoneVariableData> handle(T ptoneDatasourceDesc);

}
