package com.sizzler.proxy.widget.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.proxy.dispatcher.PtoneGraphWidgetDataDesc;
import com.sizzler.proxy.dispatcher.PtoneGraphWidgetDataHandler;
import com.sizzler.proxy.dispatcher.PtoneWidgetData;
import com.sizzler.proxy.widget.PtoneGraphWidgetDataDispatcher;

@Component
public class PtoneTableWidgetDataHandler implements
    PtoneGraphWidgetDataHandler<PtoneGraphWidgetDataDesc> {

  private Logger log = LoggerFactory.getLogger(PtoneTableWidgetDataHandler.class);

  @Autowired
  private PtoneGraphWidgetDataDispatcher ptoneGraphWidgetDataDispatcher;

  public PtoneWidgetData handle(PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc) {
    log.debug("start process PtoneTableWidgetDataDesc");
    PtoneWidgetData ptoneWidgetData =
        ptoneGraphWidgetDataDispatcher.parseTableWidgetData(ptoneGraphWidgetDataDesc);
    log.debug("end process PtoneTableWidgetDataDesc");
    return ptoneWidgetData;
  }

}
