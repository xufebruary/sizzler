package org.apache.metamodel.test;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.util.UrlResource;

/**
 * Created by ptmind on 2015/11/27.
 */
public class TestMain {
  public static void main(String[] args) {
    String url =
        "https://docs.google.com/spreadsheets/export?id=1C2WYyqs1nB2kYzY2He0zyiZqNQFEcXjBEBbyX60TlUw&exportFormat=xlsx";
    UrlResource urlResource = new UrlResource(url);

  }
}
