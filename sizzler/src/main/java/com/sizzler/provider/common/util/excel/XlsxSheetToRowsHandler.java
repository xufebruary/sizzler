package com.sizzler.provider.common.util.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.metamodel.util.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sizzler.common.utils.StringUtil;

/**
 * Created by xin.zhang on 2016/6/24.
 */
public class XlsxSheetToRowsHandler extends DefaultHandler {

  private final static Logger logger = LoggerFactory.getLogger(XlsxSheetToRowsHandler.class);

  private static enum XssfDataType {
    BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
  }

  private static String default_dateformat = "yyyy-MM-dd HH:mm:ss";

  private StylesTable _stylesTable;
  private SharedStringsTable _sharedStringTable;
  private int _rowNumber;
  private List<String> rowlist = new ArrayList<String>();
  private List<List<String>> allRowList = new ArrayList<List<String>>();
  private int curCol = 0;
  private StringBuilder _value;
  private int maxCols = 0;

  private XssfDataType _dataType;
  private int _formatIndex;
  private String _formatString;

  /*
   * 如果cell中是函数，那么 <c>下面会包含<f>和<v>两个子元素，为了只取得<v>中的值，则需要使用 _inCell和_inFormula 两个参数来标识cell是否为函数 <c
   * r="A2" t="str"> <f t="array" ref="A2">REGEXEXTRACT("hello world","hello")</f> <v>hello</v> </c>
   */
  private boolean _inCell;
  private boolean _inFormula;

  private int _columnNumber;

  public XlsxSheetToRowsHandler(XSSFReader xssfReader) throws Exception {
    _sharedStringTable = xssfReader.getSharedStringsTable();
    _stylesTable = xssfReader.getStylesTable();

    _value = new StringBuilder();
    _inCell = false;
    _inFormula = false;
    _rowNumber = -1;
  }


  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {

    if ("row".equals(qName)) {
      // excel row numbers are 1-based
      int rowNumber = Integer.parseInt(attributes.getValue("r"));
      rowNumber = rowNumber - 1;

      while (_rowNumber + 1 < rowNumber) {
        _rowNumber++;
        ArrayList<String> tmpRowList = new ArrayList<String>();
        allRowList.add(tmpRowList);
      }

      _rowNumber = rowNumber;

    }
    // c => 单元格
    else if ("c".equals(qName)) {

      // 代表进入<c>中
      _inCell = true;

      String r = attributes.getValue("r");
      int firstDigit = -1;
      for (int c = 0; c < r.length(); ++c) {
        if (Character.isDigit(r.charAt(c))) {
          firstDigit = c;
          break;
        }
      }

      _columnNumber = nameToColumn(r.substring(0, firstDigit));

      // 将cell的默认数据类型设置为 Number
      _dataType = XssfDataType.NUMBER;
      _formatIndex = -1;
      _formatString = null;

      // 取得cell的实际数据类型,当cell中不包含 "t" ，则表明 数据类型为 Number
      String cellType = attributes.getValue("t");
      if (cellType != null) {
        if ("b".equals(cellType)) {
          _dataType = XssfDataType.BOOL;
        } else if ("e".equals(cellType)) {
          _dataType = XssfDataType.ERROR;
        } else if ("inlineStr".equals(cellType)) {
          _dataType = XssfDataType.INLINESTR;
        } else if ("s".equals(cellType)) {
          _dataType = XssfDataType.SSTINDEX;
        } else if ("str".equals(cellType)) {
          _dataType = XssfDataType.FORMULA;
        }
      }


      // 取得cell的数据format
      String cellStyleStr = attributes.getValue("s");
      if (cellStyleStr != null) {
        // It's a number, but almost certainly one
        // with a special style or format
        int styleIndex = Integer.parseInt(cellStyleStr);
        XSSFCellStyle style = _stylesTable.getStyleAt(styleIndex);

        // configureStyle(style);
        // 当cell的数据类型为Number时，取出cell的 format
        if (_dataType == XssfDataType.NUMBER) {
          this._formatIndex = style.getDataFormat();
          this._formatString = style.getDataFormatString();
          if (this._formatString == null) {
            this._formatString = BuiltinFormats.getBuiltinFormat(this._formatIndex);
          }
        }
      }

    } else if (_inCell && "f".equals(qName)) {
      // skip the actual formula line
      _inFormula = true;
    }

  }

  public void endElement(String uri, String localName, String qName) throws SAXException {

    // 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
    if ("c".equals(qName)) {
      _inCell = false;

      while (rowlist.size() < _columnNumber) {
        rowlist.add("");
      }
      String value = _value != null ? _value.toString() : "";
      try {
        value = createValue();
      } catch (Exception e) {
        e.printStackTrace();
      }

      rowlist.add(_columnNumber, value);
      // curCol++;
      _value.setLength(0);

    } else if (_inFormula && "f".equals(qName)) {
      _inFormula = false;
    } else if ("row".equals(qName)) {
      // 如果标签名称为 row ，这说明已到行尾
      allRowList.add(rowlist);
      rowlist = new ArrayList<String>();
      if (_columnNumber > maxCols) {
        maxCols = _columnNumber;
      }
      curCol = 0;
    }
  }

  // 该函数会在 计算 <></>之间的值时进行调用
  /*
   * 比如下面的结构，characters会在计算 <v>234.0</v>中的234.0时进行调用 <c r="B1" s="1"> <v>234.0</v> </c>
   * 
   * 下面的结构，characters会在计算 <f t="array" ref="A2">REGEXEXTRACT("hello world","hello")</f> 和
   * <v>hello</v>时分别进行调用 计算结果为 REGEXEXTRACT("hello world","hello")hello，所以为了跳过
   * REGEXEXTRACT("hello world","hello")，需要进行判断 <c r="A2" t="str"> <f t="array"
   * ref="A2">REGEXEXTRACT("hello world","hello")</f> <v>hello</v> </c>
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    // 得到单元格内容的值
    if (_inCell && !_inFormula) {
      _value.append(ch, start, length);
    }
  }

  public List<List<String>> getAllRowList() {
    return allRowList;
  }

  public void setAllRowList(List<List<String>> allRowList) {
    this.allRowList = allRowList;
  }


  public int getMaxCols() {
    return maxCols;
  }


  public void setMaxCols(int maxCols) {
    this.maxCols = maxCols;
  }


  private String createValue() {
    if (_value.length() == 0) {
      return "";
    }

    switch (_dataType) {
      case BOOL:
        char first = _value.charAt(0);
        return first == '0' ? "false" : "true";
      case ERROR:
        logger.warn("Error-cell occurred: {}", _value);
        return trimString(_value.toString());
      case FORMULA:
        return trimString(_value.toString());
      case INLINESTR:
        XSSFRichTextString rtsi = new XSSFRichTextString(_value.toString());
        return rtsi.toString();
      case SSTINDEX:
        String sstIndex = _value.toString();
        int idx = Integer.parseInt(sstIndex);
        XSSFRichTextString rtss = new XSSFRichTextString(_sharedStringTable.getEntryAt(idx));
        return trimString(rtss.toString());
      case NUMBER:
        String numberString = _value.toString();
        if (_formatString != null) {
          String dataFormat = PoiBuiltinFormats.getDataFormat(_formatString);
          if (dataFormat == null) {
            dataFormat = fixDataFormat(_formatString);
            System.out.println("dataFormat not support:" + _formatString + ",fix:" + dataFormat);
          }

          DataFormatter formatter = getDataFormatter();
          if (HSSFDateUtil.isADateFormat(_formatIndex, _formatString)
              || XlsxSheetToRowsHandler.isADateFormat(_formatString)) {
            Date date = DateUtil.getJavaDate(Double.parseDouble(numberString));
            // todo:需要指定日期的格式

            if (dataFormat == null) {
              return numberString;
            }
            return DateUtils.createDateFormat(dataFormat).format(date);
          }
          if (_formatString.startsWith("reserved")) {
            return numberString;
          }
          return formatter.formatRawCellContents(Double.parseDouble(numberString), _formatIndex,
              dataFormat);
        } else {

         /*
          * 暂时注释掉，避免造成20160202被解析成2.0160202E7
          * modify by you.zou 2016.12.26
          *  try {
            numberString = Double.parseDouble(numberString) + "";
          } catch (Exception e) {
            logger.info("parse " + numberString + " as double error!");
          }*/

          if (numberString.endsWith(".0")) {
            // xlsx only stores doubles, so integers get ".0" appended
            // to them
            return numberString.substring(0, numberString.length() - 2);
          }
          return numberString;
        }
      default:
        logger.error("Unsupported data type: {}", _dataType);
        return "";
    }
  }

  private DataFormatter getDataFormatter() {
    return new DataFormatter();
  }

  /**
   * 清理字符串前后的空格
   */
  private static String trimString(String str){
    if(StringUtil.isBlank(str)){
      return "";
    }
    return str.trim();
  }
  
  public static String fixDataFormat(String dataFormat) {
    String newDataFormat = dataFormat;
    if (dataFormat != null) {
      String[] dataFormatArray = dataFormat.split(";");
      if (dataFormatArray.length > 0) {
        String tmpDataFormat = dataFormatArray[0];
        newDataFormat =
            tmpDataFormat.replaceAll("_-", "").replace("_", "").replace("(", "").replace(")", "")
                .replace("\"", "").replace("\\", "").replace("*", "").replace("AM/PM", "a")
                .replace("PM/AM", "a").replace("AM", "a").replace("PM", "a").replace("AM(PM)", "a")
                .replace("PM(AM)", "a").replace("am/pm", "a").replace("pm/am", "a")
                .replace("am(pm)", "a").replace("pm(am)", "a").replace("a/p", "a")
                .replace("A/P", "a")
                // //去掉 [$-409]mmm\-yy;@ 中的 [$-409]，该结构的字符串一般会在 reserved- 的 dataFormat中存在
                .replaceAll("\\[\\$\\-.*\\]", "");


      }

      if (newDataFormat.contains("yy") && !newDataFormat.contains("yyyy")) {
        newDataFormat = newDataFormat.replace("yy", "yyyy");
      }

      // 分钟的不进行处理
      if (newDataFormat.contains(":mm") || newDataFormat.contains("mm:")) {

      } else // 其他情况的m需要转换为M
      {
        newDataFormat = newDataFormat.replace("m", "M");
      }

      // // 处理自定义货币类型,去掉 [$ ]
      // if(dataFormat.matches(".*\\[\\$.*\\].*")){
      // newDataFormat = dataFormat.replace("[$", "").replace("]", "");
      // }
    }

    return newDataFormat;
  }


  /**
   * 判断包含 年、月、日、时、分、秒、時的日期
   * @param dateFormat
   * @return
   * @date: 2016年6月27日
   * @author peng.xu
   */
  public static boolean isADateFormat(String dateFormat) {
    String[] dateStr = new String[] {"年", "月", "日", "时", "分", "秒", "時"};
    for (String d : dateStr) {
      if (dateFormat.contains(d)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Converts an Excel column name like "C" to a zero-based index.
   *
   * @param name
   * @return Index corresponding to the specified name
   */
  private int nameToColumn(String name) {
    int column = -1;
    for (int i = 0; i < name.length(); ++i) {
      int c = name.charAt(i);
      column = (column + 1) * 26 + c - 'A';
    }
    return column;
  }

}
