package com.sizzler.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.EditorDataProvider;
import com.sizzler.provider.common.EditorDataRequest;
import com.sizzler.provider.common.EditorDataResponse;

@Service("editorDataProvider")
public class EditorDataProviderImpl implements EditorDataProvider {

  @Autowired
  private EditorDataProvider dataBaseProbider;
  
  @Override
  public EditorDataResponse getEditorData(EditorDataRequest request) {

//    EditorDataProvider editorDataProvider =
//        ExtensionLoader.getExtensionLoader(EditorDataProvider.class).getAdaptiveExtension();
//    return editorDataProvider.getEditorData(request);

    return dataBaseProbider.getEditorData(request);
  }

}
