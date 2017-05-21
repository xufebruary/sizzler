package com.sizzler.provider.adaptive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.common.extension.PtoneAdaptive;
import com.sizzler.provider.common.EditorDataProvider;
import com.sizzler.provider.common.EditorDataRequest;
import com.sizzler.provider.common.EditorDataResponse;

@PtoneAdaptive
public class EditorDataProviderAdaptive implements EditorDataProvider {
  private static final Logger log = LoggerFactory.getLogger(EditorDataProviderAdaptive.class);

  @Override
  public EditorDataResponse getEditorData(EditorDataRequest request) {
    log.info("execute EditorDataProviderAdaptive getEditorData");
    EditorDataProvider editorDataProvider = null;
    ExtensionLoader<EditorDataProvider> extensionLoader = ExtensionLoader
        .getExtensionLoader(EditorDataProvider.class);
    String dsCode = request.getUserConnection().getDsCode();
    if (dsCode != null && dsCode.length() > 0) {
      editorDataProvider = extensionLoader.getExtension(dsCode);
    }

    if (editorDataProvider == null) {
      throw new IllegalStateException("Can't init dsCode=" + dsCode + "'s EditorDataProvider ");
    }
    return editorDataProvider.getEditorData(request);
  }

}
