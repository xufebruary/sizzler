package com.sizzler.common.extension.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.sizzler.common.extension.ExtensionFactory;
import com.sizzler.common.extension.ExtensionLoader;
import com.sizzler.common.extension.PtoneAdaptive;

@PtoneAdaptive
public class AdaptiveExtensionFactory implements ExtensionFactory {

  private final List<ExtensionFactory> objectFactories;

  public AdaptiveExtensionFactory() {
    ExtensionLoader<ExtensionFactory> extensionLoader = ExtensionLoader
        .getExtensionLoader(ExtensionFactory.class);
    List<ExtensionFactory> list = new ArrayList<>();
    Set<String> nameSet = extensionLoader.getSupportedExtensions();
    for (String name : nameSet) {
      list.add(extensionLoader.getExtension(name));
    }

    objectFactories = Collections.unmodifiableList(list);
  }

  @Override
  public <T> T getExtension(Class<T> type, String name) {
    for (ExtensionFactory factory : objectFactories) {
      T extension = factory.getExtension(type, name);
      if (extension != null) {
        return extension;
      }
    }
    return null;
  }
}
