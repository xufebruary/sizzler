package com.sizzler.provider.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.util.FileHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ptmind.cpdetector.CpdetectorUtil;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.FileType;
import com.sizzler.common.store.file.FileStoreStrategy;
import com.sizzler.common.utils.ExcelTool;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.provider.common.file.PtoneFile;
import com.sizzler.provider.common.util.excel.ExcelUtil;

@Component("fileStoreUtil")
public class FileStoreUtil {

  private static final Logger log = LoggerFactory.getLogger(FileStoreUtil.class);

  @Autowired
  private FileStoreStrategy fileStoreStrategy;

  public PtoneFile downloadPtoneFileFromFileStore(String uid, String dsCode, String fileId) {
    return downloadPtoneFileFromFileStore(uid, dsCode, fileId, ".xls");
  }

  public PtoneFile downloadPtoneFileFromFileStore(String uid, String dsCode, String fileId,
      String suffix) {
    PtoneFile ptoneFile = new PtoneFile();
    ptoneFile.setId(fileId);
    String destPath = fileStoreStrategy.buildFilePath(uid + "/" + dsCode, fileId, suffix);
    try {
      InputStream inputStream = fileStoreStrategy.getReadFileInputStream(destPath);
      LinkedHashMap<String, List<List>> fileListDataMap = ExcelTool.getExcelContent(inputStream);
      ptoneFile.setFileListDataMap(fileListDataMap);
    } catch (Exception e) {
      e.printStackTrace();
      log.info("read '" + destPath + "' from file store error");
      return null;
    }
    return ptoneFile;
  }

  public PtoneFile getPtoneFileFormFileStore(String path, String fileId, String fileName,
      String type, String spliter, Boolean maxRowLimit) throws Exception {
    PtoneFile ptoneFile = null;
    InputStream inputStream = null;
    ByteArrayOutputStream swapStream = null;
    ByteArrayInputStream byteArrayInputStream = null;
    try {

      maxRowLimit = maxRowLimit == null ? true : maxRowLimit;

      inputStream = fileStoreStrategy.getReadFileInputStream(path);
      FileType fileType = FileType.getFileTypeByName(type);
      if (fileType == FileType.EXCEL) {
        ptoneFile = ExcelUtil.convertExcelToPtoneFile(fileName, inputStream, maxRowLimit);
      } else if (fileType == FileType.CSV || fileType == FileType.TSV || fileType == FileType.TXT) {
        swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 10]; // buff用于存放循环读取的临时数据
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
          swapStream.write(buff, 0, rc);
        }
        byteArrayInputStream = new ByteArrayInputStream(swapStream.toByteArray());

        // 先监测编码
        Charset charset = CpdetectorUtil.determineChartSet(byteArrayInputStream);

        ptoneFile = SpliterFileUtil.convertSpliterFileToPtoneFile(fileName, byteArrayInputStream,
            spliter, maxRowLimit, charset);
      }
      if (ptoneFile != null && StringUtil.isBlank(ptoneFile.getId())) {
        ptoneFile.setId(fileId);
      }
    } finally {
      FileHelper.safeClose(byteArrayInputStream, swapStream, inputStream);
    }
    return ptoneFile;
  }

  public void uploadPtoneFileToHdfs(PtoneFile ptoneFile, String uid, String dsCode) {
    // 默认情况下 同步上传到HDFS上面
    uploadPtoneFileToHdfs(ptoneFile, uid, dsCode, true);
  }

  public void uploadPtoneFileToHdfs(PtoneFile ptoneFile, String uid, String dsCode, boolean syn) {
    // 默认的文件名后缀为.xls
    uploadPtoneFileToHdfs(ptoneFile, uid, dsCode, ".xls", syn);
  }

  public void uploadPtoneFileToHdfs(PtoneFile ptoneFile, String uid, String dsCode,
      String fileExtension, boolean syn) {
    String filePath = uid + "/" + dsCode;
    String fileId = null;
    if (dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLESHEET)
        || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLEDRIVE)
        || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_S3)) {
      fileId = ptoneFile.getId();
    } else {
      fileId = ptoneFile.getName();
    }
    String destPath = fileStoreStrategy.buildFilePath(filePath, fileId, fileExtension);

    LinkedHashMap<String, List<List>> fileListDataMap = new LinkedHashMap<>();
    for (Map.Entry<String, List<List>> entry : ptoneFile.getFileListDataMap().entrySet()) {
      String key = entry.getKey();
      List<List> value = entry.getValue();
      List<List> newResultList = new ArrayList<>();
      for (List list : value) {
        List tmpList = new ArrayList();
        tmpList.addAll(list);
        newResultList.add(tmpList);
      }
      fileListDataMap.put(key, newResultList);
    }
    if (syn) {
      writeExcelToFileStore(destPath, ptoneFile.getFileListDataMap());
    } else // 异步情况下，单独的启动一个线程来将文件上传到HDFS
    {
      // 单独的起一个进程来进行文件的上传
      new Thread(new WriteDataToHdfsThread(destPath, fileListDataMap)).start();
    }

  }

  public class WriteDataToHdfsThread implements Runnable {
    String destPath;
    LinkedHashMap<String, List<List>> fileListDataMap;

    public WriteDataToHdfsThread(String destPath, LinkedHashMap<String, List<List>> fileListDataMap) {
      this.destPath = destPath;
      this.fileListDataMap = fileListDataMap;
    }

    @Override
    public void run() {
      log.info("开始异步的上传文件：" + destPath);
      writeExcelToFileStore(destPath, fileListDataMap);
    }
  }

  public void writeMetaModelRowToFileStore(String filePath,
      LinkedHashMap<String, List<Row>> fileDataMap) {
    LinkedHashMap<String, List<List>> fileDataListMap = new LinkedHashMap<>();
    for (Map.Entry<String, List<Row>> entry : fileDataMap.entrySet()) {
      String key = entry.getKey();
      List<List> tmpRowList = new ArrayList<>();
      List<Row> rowList = entry.getValue();
      for (Row row : rowList) {
        tmpRowList.add(Arrays.asList(row.getValues()));
      }
      fileDataListMap.put(key, tmpRowList);
    }
    writeExcelToFileStore(filePath, fileDataListMap);
  }

  public void writeExcelToFileStore(String filePath, LinkedHashMap<String, List<List>> fileDataMap) {
    OutputStream outputStream = null;
    XSSFWorkbook wb = null;
    try {
      outputStream = fileStoreStrategy.getWriteFileOutputStream(filePath);
      wb = new XSSFWorkbook();
      if (fileDataMap != null) {
        for (Map.Entry<String, List<List>> entry : fileDataMap.entrySet()) {
          String sheetName = entry.getKey();
          List<List> rowList = entry.getValue();
          XSSFSheet sheet = wb.createSheet(sheetName);
          int rowCount = rowList.size();
          for (int i = 0; i < rowCount; i++) {
            XSSFRow xssfrow = sheet.createRow(i);
            List row = rowList.get(i);
            for (int j = 0; j < row.size(); j++) {
              xssfrow.createCell(j).setCellValue(row.get(j).toString());
            }
          }
        }
      }
      wb.write(outputStream);
    } catch (Exception e) {
      log.error("write file<{}> error: " + e.getMessage(), filePath, e);
    } finally {
      FileHelper.safeClose(wb, outputStream);
    }
  }

}
