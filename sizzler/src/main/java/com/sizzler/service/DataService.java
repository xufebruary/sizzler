package com.sizzler.service;

import java.util.Map;

import com.sizzler.domain.widget.dto.AcceptWidget;

public interface DataService {
  
  public void addDataTask(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget);

}
