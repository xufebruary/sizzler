package com.sizzler.provider.common.util.excel;

import org.apache.metamodel.MetaModelException;
import org.apache.poi.POIXMLDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.XMLReader;

import com.ptmind.common.utils.CollectionUtil;
import com.ptmind.common.utils.StringUtil;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by xin.zhang on 2016/6/24.
 */
public class ExcelUtil2 {

  private static final Logger logger = LoggerFactory.getLogger(ExcelUtil2.class);

  public static Map<String, Object> createSchema(String fileName, InputStream inputStream,
      boolean maxRowLimit) throws Exception {
    if (!inputStream.markSupported()) {
      inputStream = new PushbackInputStream(inputStream, 8);
    }

    ExcelReaderDelegate excelReaderDelegate = getExcelReaderDelegate(inputStream);
    return excelReaderDelegate.createSchema(fileName, inputStream, maxRowLimit);
  }

  private static ExcelReaderDelegate getExcelReaderDelegate(InputStream inputStream) {


    ExcelReaderDelegate excelReaderDelegate = null;
    try {
      if (POIXMLDocument.hasOOXMLHeader(inputStream)) {
        excelReaderDelegate = new XlsxExcelReaderDelegate();
      } else {
        excelReaderDelegate = new XlsExcelReaderDelegate(inputStream);
      }
    } catch (IOException e) {
      logger.warn("Could not identify spreadsheet type ,", e);
    }

    return excelReaderDelegate;

  }

  public static XMLReader createXmlReader() {
    try {
      SAXParserFactory saxFactory = SAXParserFactory.newInstance();
      SAXParser saxParser = saxFactory.newSAXParser();
      XMLReader sheetParser = saxParser.getXMLReader();
      return sheetParser;
    } catch (Exception e) {
      throw new MetaModelException(e);
    }
  }
  
  /**
   * 从后往前清理行数据中空串的列，直到找到最后一个不为空的列，该列索引就是该行最大的列数<br>
   * 从行列表中找到最大的列数
   * @author you.zou
   * @date 2016年9月21日 上午10:14:51
   * @param arrayList
   * @return
   */
  public static int removeEmptyValueOfListThenReturnMaxSize(List<List<String>> rowList){
    int maxSize = 0;
    if(CollectionUtil.isEmpty(rowList)){
     return maxSize; 
    }
    for(List<String> row : rowList){
      if(CollectionUtil.isEmpty(row)){continue;}
      int childSize = row.size();
      for(int i=childSize-1; i>=0; i--){
        String childValue = row.get(i);
        if(StringUtil.isBlank(childValue)){
          //childList中清理掉空串
          row.remove(i);
        }else{
          int nowSize = i+1;
          //最后一个没有空串的
          if(maxSize < nowSize){
            maxSize = nowSize;
          }
          break;
        }
      }
    }
    return maxSize;
  }
  
  /**
   * 验证二维表是否是一个空表，并返回最后一个有数据的行号<br>
   * 空表验证规则：某一行不为空<br>
   * 有数据的行号：由后往前检查行中是否每一列都是空串，<br>
   * 如果是空串，则当前是空行，如果不是空串，则当前是最后 一行有数据的行，返回当前行号
   * @author you.zou
   * @date 2016年9月21日 上午11:58:18
   * @param allRowList
   * @return Object[0] == lastRowNum, Object[1] == isEmptyTable
   */
  public static Object[] checkIsEmptyTableAndGetLastRowNum(List<List<String>> allRowList){
    boolean isEmptyTable = true;
    boolean isEmptyRow = true;
    //原代码为：int lastRowNum = allRowList.size();
    //原代码是错误的，因为默认的最后行号如果是allRowList的大小
    //那么当所有行的列数据都是空串时，最后的行号会是allRowList大小，但正确结果应该是0
    int lastRowNum = 0;
    for (int i = allRowList.size() - 1; i >= 0; i--) {

      List<String> tmpRow = allRowList.get(i);

      //如果该行没有列数据，则不需要再往下进行了
      if(CollectionUtil.isEmpty(tmpRow)){
        continue;
      }
      
      for (String tmpCol : tmpRow) {
        if (!StringUtil.isBlank(tmpCol)) {
          isEmptyRow = false;
          break;
        }
      }
      if (!isEmptyRow) {
        lastRowNum = i + 1;
        //原本的判断逻辑是：if (tmpRow.size > 0){ isEmptyTable = false;} 
        //原逻辑是错误的，因为如果二维表中所有行的所有列都是空串的时候
        //实际上这个二维表是个空表，但原逻辑会设置成isEmptyTable=false
        //而放到if (!isEmptyRow) 中则不存在空表的问题，因为能进入该判断就证明了该二维表不是个空表，不进入就证明是空表
        isEmptyTable = false;
        break;
      }
    }
    return new Object[]{lastRowNum, isEmptyTable};
  }

}
