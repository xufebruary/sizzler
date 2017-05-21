package org.apache.metamodel.util;

import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ptmind on 2015/11/28.
 */
public class HdfsUtil {

  public static void upload(InputStream inputStream, String destinationPath) throws IOException {
    uploadHdfsResource(inputStream, new HdfsResource(destinationPath));
  }

  public static void uploadHdfsResource(InputStream inputStream, HdfsResource hdfsResource)
      throws IOException {
    IOUtils.copyBytes(inputStream, hdfsResource.write(), 4096, true);
  }

  public static void download(OutputStream outputStream, String destinationPath) throws IOException {
    downloadHdfsResource(outputStream, new HdfsResource(destinationPath));
  }

  public static void downloadHdfsResource(OutputStream outputStream, HdfsResource hdfsResource)
      throws IOException {
    IOUtils.copyBytes(hdfsResource.read(), outputStream, 4096, true);
  }

}
