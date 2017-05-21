package com.sizzler.common.sizzler;

public enum FileType {

  EXCEL, CSV, TSV, TXT;

  public static FileType getFileTypeByName(String name) {
    FileType fileType = TXT;
    for (FileType tmpFileType : FileType.values()) {
      if (tmpFileType.name().equalsIgnoreCase(name)) {
        fileType = tmpFileType;
        break;
      }
    }
    return fileType;
  }

}
