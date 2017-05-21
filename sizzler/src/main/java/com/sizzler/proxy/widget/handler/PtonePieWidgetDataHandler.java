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
public class PtonePieWidgetDataHandler implements
    PtoneGraphWidgetDataHandler<PtoneGraphWidgetDataDesc> {

  private Logger log = LoggerFactory.getLogger(PtonePieWidgetDataHandler.class);

  @Autowired
  private PtoneGraphWidgetDataDispatcher ptoneGraphWidgetDataDispatcher;

  public PtoneWidgetData handle(PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc) {
    log.debug("start process PtonePieWidgetDataDesc");
    PtoneWidgetData ptoneWidgetData =
        ptoneGraphWidgetDataDispatcher.parsePieWidgetData(ptoneGraphWidgetDataDesc);
    log.debug("end process PtonePieWidgetDataDesc");
    return ptoneWidgetData;
  }

}
