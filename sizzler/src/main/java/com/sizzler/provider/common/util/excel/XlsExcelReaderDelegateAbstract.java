package com.sizzler.provider.common.util.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.metamodel.util.DateUtils;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.ExcelStyleDateFormatter;

/**
 * 2003及以前版本excel的解析
 * 
 * @date: 2016年6月24日
 * @author peng.xu
 */
public abstract class XlsExcelReaderDelegateAbstract implements HSSFListener {
  private int minColumns;
  private POIFSFileSystem fs;
  private PrintStream output;

  private int lastRowNumber;
  private int lastColumnNumber;

  /** Should we output the formula, or the value it has? */
  private boolean outputFormulaValues = true;

  private boolean printCell = false;

  /** For parsing Formulas */
  private SheetRecordCollectingListener workbookBuildingListener;
  private HSSFWorkbook stubWorkbook;

  // Records we pick up as we process
  private SSTRecord sstRecord;
  private FormatTrackingHSSFListener formatListener;

  private final HSSFDataFormatter _formatter;
  private final NumberFormat _defaultFormat;

  /** So we known which sheet we're on */
  private int sheetIndex = -1;
  private BoundSheetRecord[] orderedBSRs;
  @SuppressWarnings("rawtypes")
  private ArrayList boundSheetRecords = new ArrayList();

  // For handling formulas with string results
  private int nextRow;
  private int nextColumn;
  private boolean outputNextStringRecord;

  private int curRow;
  private List<String> rowlist;
  private String sheetName;
  private boolean isHiddenSheet;

  public XlsExcelReaderDelegateAbstract(POIFSFileSystem fs) {
    this.fs = fs;
    this.output = System.out;
    this.minColumns = -1;
    this.curRow = 0;
    this.rowlist = new ArrayList<String>();

    _formatter = new HSSFDataFormatter(Locale.getDefault());
    _defaultFormat = NumberFormat.getInstance(Locale.getDefault());
  }

  public XlsExcelReaderDelegateAbstract(String filename) throws IOException, FileNotFoundException {
    this(new POIFSFileSystem(new FileInputStream(filename)));
  }

  public XlsExcelReaderDelegateAbstract(InputStream inputStream) throws IOException,
      FileNotFoundException {
    this(new POIFSFileSystem(inputStream));
  }

  // //excel记录行操作方法，以行索引和行元素列表为参数，对一行元素进行操作，元素为String类型
  // public abstract void optRows(int curRow, List<String> rowlist) throws
  // SQLException ;

  // excel记录行操作方法，以sheet索引，行索引和行元素列表为参数，对sheet的一行元素进行操作，元素为String类型
  public abstract void optRows(int sheetIndex, String sheetName, int curRow, List<String> rowlist)
      throws SQLException;

  /**
   * 遍历 excel 文件
   */
  public void process() throws IOException {
    MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
    formatListener = new FormatTrackingHSSFListener(listener);

    HSSFEventFactory factory = new HSSFEventFactory();
    HSSFRequest request = new HSSFRequest();

    if (outputFormulaValues) {
      request.addListenerForAllRecords(formatListener);
    } else {
      workbookBuildingListener = new SheetRecordCollectingListener(formatListener);
      request.addListenerForAllRecords(workbookBuildingListener);
    }

    factory.processWorkbookEvents(request, fs);
  }

  /**
   * HSSFListener 监听方法，处理 Record
   */
  @SuppressWarnings("unchecked")
  public void processRecord(Record record) {
    int thisRow = -1;
    int thisColumn = -1;
    String thisStr = null;
    String value = null;

    switch (record.getSid()) {
      case BoundSheetRecord.sid:
        boundSheetRecords.add(record);
        break;
      case BOFRecord.sid:
        BOFRecord br = (BOFRecord) record;
        if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
          // Create sub workbook if required
          if (workbookBuildingListener != null && stubWorkbook == null) {
            stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
          }

          // Works by ordering the BSRs by the location of
          // their BOFRecords, and then knowing that we
          // process BOFRecords in byte offset order
          sheetIndex++;
          if (orderedBSRs == null) {
            orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
          }
          BoundSheetRecord sheetRecord = orderedBSRs[sheetIndex];
          sheetName = sheetRecord.getSheetname();
          isHiddenSheet = sheetRecord.isHidden() || sheetRecord.isVeryHidden();
        }
        break;

      case SSTRecord.sid:
        sstRecord = (SSTRecord) record;
        break;

      case BlankRecord.sid:
        BlankRecord brec = (BlankRecord) record;

        thisRow = brec.getRow();
        thisColumn = brec.getColumn();
        rowlist.add(thisColumn, "");
        break;
      case BoolErrRecord.sid:
        BoolErrRecord berec = (BoolErrRecord) record;

        thisRow = berec.getRow();
        thisColumn = berec.getColumn();
        thisStr = "BoolErrRecord (" + sheetName + " ==> " + thisRow + "," + thisColumn + ") ";
        rowlist.add(thisColumn, "");
        break;

      case FormulaRecord.sid:
        FormulaRecord frec = (FormulaRecord) record;

        thisRow = frec.getRow();
        thisColumn = frec.getColumn();

        if (outputFormulaValues) {
          if (Double.isNaN(frec.getValue())) {
            // Formula result is a string
            // This is stored in the next record
            outputNextStringRecord = true;
            nextRow = frec.getRow();
            nextColumn = frec.getColumn();
          } else {
            thisStr = formatNumberDateCell(frec);
            rowlist.add(thisColumn, thisStr);
          }
        } else {
          thisStr =
              '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression()) + '"';
        }
        break;
      case StringRecord.sid:
        if (outputNextStringRecord) {
          // String for formula
          StringRecord srec = (StringRecord) record;
          thisStr = srec.getString();
          thisRow = nextRow;
          thisColumn = nextColumn;
          outputNextStringRecord = false;
        }
        break;

      case LabelRecord.sid:
        LabelRecord lrec = (LabelRecord) record;

        curRow = thisRow = lrec.getRow();
        thisColumn = lrec.getColumn();
        value = lrec.getValue().trim();
        value = value.equals("") ? " " : value;
        this.rowlist.add(thisColumn, value);
        break;
      case LabelSSTRecord.sid:
        LabelSSTRecord lsrec = (LabelSSTRecord) record;

        curRow = thisRow = lsrec.getRow();
        thisColumn = lsrec.getColumn();
        if (sstRecord == null) {
          rowlist.add(thisColumn, " ");
        } else {
          value = sstRecord.getString(lsrec.getSSTIndex()).toString().trim();
          value = value.equals("") ? " " : value;
          rowlist.add(thisColumn, value);
        }
        break;
      case NoteRecord.sid:
        NoteRecord nrec = (NoteRecord) record;

        thisRow = nrec.getRow();
        thisColumn = nrec.getColumn();
        // TODO: Find object to match nrec.getShapeId()
        thisStr = '"' + "(TODO)" + '"';
        break;
      case NumberRecord.sid:
        NumberRecord numrec = (NumberRecord) record;

        curRow = thisRow = numrec.getRow();
        thisColumn = numrec.getColumn();
        value = formatNumberDateCell(numrec).trim();
        value = value.equals("") ? " " : value;
        // Format
        rowlist.add(thisColumn, value);
        break;
      case RKRecord.sid:
        RKRecord rkrec = (RKRecord) record;

        thisRow = rkrec.getRow();
        thisColumn = rkrec.getColumn();
        thisStr = '"' + "RKRecord (TODO)" + '"';
        break;
      default:
        break;
    }

    // 遇到新行的操作
    if (thisRow != -1 && thisRow != lastRowNumber) {
      lastColumnNumber = -1;
    }

    // 空值的操作
    if (record instanceof MissingCellDummyRecord) {
      MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
      curRow = thisRow = mc.getRow();
      thisColumn = mc.getColumn();
      rowlist.add(thisColumn, " ");
    }

    // 如果遇到能打印的东西，在这里打印
    if (printCell && thisStr != null) {
      if (thisColumn > 0) {
        output.print(',');
      }
      output.print(thisStr);
    }

    // 更新行和列的值
    if (thisRow > -1)
      lastRowNumber = thisRow;
    if (thisColumn > -1)
      lastColumnNumber = thisColumn;

    // 行结束时的操作
    if (record instanceof LastCellOfRowDummyRecord) {
      if (minColumns > 0) {
        // 列值重新置空
        if (lastColumnNumber == -1) {
          lastColumnNumber = 0;
        }
      }
      // 行结束时， 调用 optRows() 方法
      lastColumnNumber = -1;
      try {
        if (!isHiddenSheet) {
          optRows(sheetIndex, sheetName, curRow, rowlist);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      rowlist.clear();
    }
  }


  /**
   * 从 FormatTrackingHSSFListener 中 formatNumberDateCell（） 方法复制修改而来
   * 
   * Formats the given numeric of date Cell's contents as a String, in as close as we can to the way
   * that Excel would do so. Uses the various format records to manage this.
   *
   * TODO - move this to a central class in such a way that hssf.usermodel can make use of it too
   */
  public String formatNumberDateCell(CellValueRecordInterface cell) {
    double value;
    if (cell instanceof NumberRecord) {
      value = ((NumberRecord) cell).getValue();
    } else if (cell instanceof FormulaRecord) {
      value = ((FormulaRecord) cell).getValue();
    } else {
      throw new IllegalArgumentException("Unsupported CellValue Record passed in " + cell);
    }

    // Get the built in format, if there is one
    int formatIndex = formatListener.getFormatIndex(cell);
    String formatString = formatListener.getFormatString(cell);

    String fixFormatString = PoiBuiltinFormats.getDataFormat(formatString);
    if (fixFormatString == null) {
      fixFormatString = XlsxSheetToRowsHandler.fixDataFormat(formatString);
    }

    if (fixFormatString == null) {
      return _defaultFormat.format(value);
    }

    if (fixFormatString.startsWith("reserved")) {
      return String.valueOf(value);
    }

    // 包含 年、月、日、时、分、秒、時
    if (!DateUtil.isADateFormat(formatIndex, fixFormatString)
        && XlsxSheetToRowsHandler.isADateFormat(fixFormatString)) {
      Date date = DateUtil.getJavaDate(value);
      String dateStr = "";
      try {
        dateStr = DateUtils.createDateFormat(fixFormatString).format(date);
      } catch (Exception e) {
        System.out.println("dateFormat not support:" + formatString + ",fix:" + fixFormatString);
      }
      return dateStr;
    }

    // Format, using the nice new
    // HSSFDataFormatter to do the work for us
    return _formatter.formatRawCellContents(value, formatIndex, fixFormatString);
  }
}
