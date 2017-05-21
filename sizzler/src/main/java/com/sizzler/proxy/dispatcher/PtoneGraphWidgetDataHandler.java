package com.sizzler.proxy.dispatcher;


public interface PtoneGraphWidgetDataHandler<T extends PtoneGraphWidgetDataDesc> {

  public PtoneWidgetData handle(T ptoneData);

}
