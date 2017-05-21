package com.sizzler.service.impl;

import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.domain.widget.PtoneWidgetInfoExtend;
import com.sizzler.service.WidgetExtendService;

@Service("widgetExtendService")
public class WidgetExtendServiceImpl extends
    ServiceBaseInterfaceImpl<PtoneWidgetInfoExtend, String> implements WidgetExtendService {

}
