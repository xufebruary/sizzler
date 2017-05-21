/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.metamodel.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import org.apache.metamodel.MetaModelException;
import org.apache.metamodel.QueryPostprocessDataContext;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.EmptyDataSet;
import org.apache.metamodel.query.FilterItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.util.FileHelper;
import org.apache.metamodel.util.FileResource;
import org.apache.metamodel.util.Func;
import org.apache.metamodel.util.Resource;
import org.apache.metamodel.util.ResourceUtils;
import org.apache.metamodel.util.UrlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

/**
 * DataContext implementation for reading CSV files.
 */
public final class CsvDataContext extends QueryPostprocessDataContext implements
    UpdateableDataContext {

  private static final Logger logger = LoggerFactory.getLogger(CsvDataContext.class);

  private final Object WRITE_LOCK = new Object();

  private final Resource _resource;
  private final CsvConfiguration _configuration;
  private final boolean _writable;

  /**
   * Constructs a CSV DataContext based on a file
   *
   * The file provided can be either existing or non-existing. In the case of non-existing files, a
   * file will be automatically created when a CREATE TABLE update is executed on the DataContext.
   * 
   * @param file
   * @param configuration
   */
  public CsvDataContext(File file, CsvConfiguration configuration) {
    if (file == null) {
      throw new IllegalArgumentException("File cannot be null");
    }
    if (configuration == null) {
      throw new IllegalArgumentException("CsvConfiguration cannot be null");
    }
    _resource = new FileResource(file);
    _configuration = configuration;
    _writable = true;
  }

  public CsvDataContext(Resource resource, CsvConfiguration configuration) {
    if (resource == null) {
      throw new IllegalArgumentException("File cannot be null");
    }
    if (configuration == null) {
      throw new IllegalArgumentException("CsvConfiguration cannot be null");
    }
    _resource = resource;
    _configuration = configuration;
    _writable = !resource.isReadOnly();
  }

  /**
   * Constructs a CSV DataContext based on a {@link URL}
   * 
   * @param url
   * @param configuration
   */
  public CsvDataContext(URL url, CsvConfiguration configuration) {
    _resource = new UrlResource(url);
    _configuration = configuration;
    _writable = false;
  }

  /**
   * Constructs a CSV DataContext based on a file
   * 
   * @param file
   */
  public CsvDataContext(File file) {
    this(file, new CsvConfiguration());
  }

  public CsvDataContext(Resource resource) {
    this(resource, new CsvConfiguration());
  }

  /**
   * Constructs a CSV DataContext based on an {@link InputStream}
   * 
   * @param inputStream
   * @param configuration
   */
  public CsvDataContext(InputStream inputStream, CsvConfiguration configuration) {
    File file = createFileFromInputStream(inputStream, configuration.getEncoding());
    _configuration = configuration;
    _writable = false;
    _resource = new FileResource(file);
  }

  /**
   * @deprecated use {@link #CsvDataContext(File, CsvConfiguration)} instead.
   */
  @Deprecated
  public CsvDataContext(File file, char separatorChar) {
    this(file, separatorChar, CsvConfiguration.DEFAULT_QUOTE_CHAR);
  }

  /**
   * @deprecated use {@link #CsvDataContext(File, CsvConfiguration)} instead.
   */
  @Deprecated
  public CsvDataContext(File file, char separatorChar, char quoteChar) {
    this(file,
        new CsvConfiguration(CsvConfiguration.DEFAULT_COLUMN_NAME_LINE,
            FileHelper.DEFAULT_ENCODING, separatorChar, quoteChar,
            CsvConfiguration.DEFAULT_ESCAPE_CHAR));
  }

  /**
   * @deprecated use {@link #CsvDataContext(File, CsvConfiguration)} instead.
   */
  @Deprecated
  public CsvDataContext(File file, char separatorChar, char quoteChar, String encoding) {
    this(file, new CsvConfiguration(CsvConfiguration.DEFAULT_COLUMN_NAME_LINE, encoding,
        separatorChar, quoteChar, CsvConfiguration.DEFAULT_ESCAPE_CHAR));
  }

  /**
   * @deprecated use {@link #CsvDataContext(URL, CsvConfiguration)} instead.
   */
  @Deprecated
  public CsvDataContext(URL url, char separatorChar, char quoteChar) {
    this(url, separatorChar, quoteChar, FileHelper.DEFAULT_ENCODING);
  }

  /**
   * @deprecated use {@link #CsvDataContext(URL, CsvConfiguration)} instead.
   */
  @Deprecated
  public CsvDataContext(URL url, char separatorChar, char quoteChar, String encoding) {
    this(url, new CsvConfiguration(CsvConfiguration.DEFAULT_COLUMN_NAME_LINE, encoding,
        separatorChar, quoteChar, CsvConfiguration.DEFAULT_ESCAPE_CHAR));
  }

  /**
   * @deprecated use {@link #CsvDataContext(InputStream, CsvConfiguration)} instead.
   */
  @Deprecated
  public CsvDataContext(InputStream inputStream, char separatorChar, char quoteChar) {
    this(inputStream,
        new CsvConfiguration(CsvConfiguration.DEFAULT_COLUMN_NAME_LINE,
            FileHelper.DEFAULT_ENCODING, separatorChar, quoteChar,
            CsvConfiguration.DEFAULT_ESCAPE_CHAR));
  }

  /**
   * @deprecated use {@link #CsvDataContext(InputStream, CsvConfiguration)} instead.
   */
  @Deprecated
  public CsvDataContext(InputStream inputStream, char separatorChar, char quoteChar, String encoding) {
    this(inputStream, new CsvConfiguration(CsvConfiguration.DEFAULT_COLUMN_NAME_LINE, encoding,
        separatorChar, quoteChar, CsvConfiguration.DEFAULT_ESCAPE_CHAR));
  }

  /**
   * Gets the CSV configuration used
   * 
   * @return a CSV configuration
   */
  public CsvConfiguration getConfiguration() {
    return _configuration;
  }

  /**
   * Gets the CSV file being read
   * 
   * @return a file
   * 
   * @deprecated use {@link #getResource()} instead.
   */
  @Deprecated
  public File getFile() {
    if (_resource instanceof FileResource) {
      return ((FileResource) _resource).getFile();
    }
    return null;
  }

  /**
   * Gets the resource that is being read from.
   * 
   * @return
   */
  public Resource getResource() {
    return _resource;
  }

  private static File createFileFromInputStream(InputStream inputStream, String encoding) {
    final File file;
    final File tempDir = FileHelper.getTempDir();

    File fileCandidate = null;
    boolean usableName = false;
    int index = 0;

    while (!usableName) {
      index++;
      fileCandidate = new File(tempDir, "metamodel" + index + ".csv");
      usableName = !fileCandidate.exists();
    }
    file = fileCandidate;

    final BufferedWriter writer = FileHelper.getBufferedWriter(file, encoding);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    try {
      file.createNewFile();
      file.deleteOnExit();

      boolean firstLine = true;

      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (firstLine) {
          firstLine = false;
        } else {
          writer.write('\n');
        }
        writer.write(line);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      FileHelper.safeClose(writer, reader);
    }

    return file;
  }

  @Override
  protected Number executeCountQuery(Table table, List<FilterItem> whereItems,
      boolean functionApproximationAllowed) {
    if (!functionApproximationAllowed) {
      return null;
    }

    if (whereItems != null && !whereItems.isEmpty()) {
      return null;
    }

    final long length = _resource.getSize();
    if (length < 0) {
      // METAMODEL-30: Sometimes the size of the resource is not known
      return null;
    }

    return _resource.read(new Func<InputStream, Number>() {
      @Override
      public Number eval(InputStream inputStream) {
        try {
          // read up to 5 megs of the file and approximate number of
          // lines based on that.

          final int sampleSize = (int) Math.min(length, 1024 * 1024 * 5);
          final int chunkSize = Math.min(sampleSize, 1024 * 1024);

          int readSize = 0;
          int newlines = 0;
          int carriageReturns = 0;
          byte[] byteBuffer = new byte[chunkSize];
          char[] charBuffer = new char[chunkSize];

          while (readSize < sampleSize) {
            final int read = inputStream.read(byteBuffer);
            if (read == -1) {
              break;
            } else {
              readSize += read;
            }

            Reader reader = getReader(byteBuffer, _configuration.getEncoding());
            reader.read(charBuffer);
            for (char c : charBuffer) {
              if ('\n' == c) {
                newlines++;
              } else if ('\r' == c) {
                carriageReturns++;
              }
            }
          }

          int lines = Math.max(newlines, carriageReturns);

          logger.info("Found {} lines breaks in {} bytes", lines, sampleSize);

          long approxCount = (long) (lines * length / sampleSize);
          return approxCount;
        } catch (IOException e) {
          logger.error("Unexpected error during COUNT(*) approximation", e);
          throw new IllegalStateException(e);
        }
      }
    });
  }

  private Reader getReader(byte[] byteBuffer, String encoding) throws UnsupportedEncodingException {
    try {
      return new InputStreamReader(new ByteArrayInputStream(byteBuffer), encoding);
    } catch (UnsupportedEncodingException e1) {
      // this may happen on more exotic encodings, but since this reader
      // is only meant for finding newlines, we'll try again with UTF8
      try {
        return new InputStreamReader(new ByteArrayInputStream(byteBuffer), "UTF8");
      } catch (UnsupportedEncodingException e2) {
        throw e1;
      }
    }
  }

  @Override
  public DataSet materializeMainSchemaTable(Table table, Column[] columns, int maxRows) {
    final int lineNumber = _configuration.getColumnNameLineNumber();
    final int columnCount = table.getColumnCount();

    final BufferedReader reader =
        FileHelper.getBufferedReader(_resource.read(), _configuration.getEncoding());

    try {
      // skip column header lines
      for (int i = 0; i < lineNumber; i++) {
        String line = reader.readLine();
        if (line == null) {
          FileHelper.safeClose(reader);
          return new EmptyDataSet(columns);
        }
      }
    } catch (IOException e) {
      FileHelper.safeClose(reader);
      throw new MetaModelException("IOException occurred while reading from CSV resource: "
          + _resource, e);
    }

    final boolean failOnInconsistentRowLength = _configuration.isFailOnInconsistentRowLength();

    final Integer maxRowsOrNull = (maxRows > 0 ? maxRows : null);

    if (_configuration.isMultilineValues()) {
      final CSVReader csvReader = createCsvReader(reader);
      return new CsvDataSet(csvReader, columns, maxRowsOrNull, columnCount,
          failOnInconsistentRowLength);
    }

    final CSVParser csvParser =
        new CSVParser(_configuration.getSeparatorChar(), _configuration.getQuoteChar(),
            _configuration.getEscapeChar());
    return new SingleLineCsvDataSet(reader, csvParser, columns, maxRowsOrNull, columnCount,
        failOnInconsistentRowLength);
  }

  protected CSVReader createCsvReader(int skipLines) {
    final Reader reader = FileHelper.getReader(_resource.read(), _configuration.getEncoding());
    final CSVReader csvReader =
        new CSVReader(reader, _configuration.getSeparatorChar(), _configuration.getQuoteChar(),
            _configuration.getEscapeChar(), skipLines);
    return csvReader;
  }

  protected CSVReader createCsvReader(BufferedReader reader) {
    final CSVReader csvReader =
        new CSVReader(reader, _configuration.getSeparatorChar(), _configuration.getQuoteChar(),
            _configuration.getEscapeChar());
    return csvReader;
  }

  @Override
  protected CsvSchema getMainSchema() throws MetaModelException {
    CsvSchema schema = new CsvSchema(getMainSchemaName(), this);
    if (_resource.isExists()) {
      String tableName = _resource.getName();
      if (tableName.endsWith(".csv")) {
        tableName = tableName.substring(0, tableName.indexOf(".csv"));
      }
      // schema.setTable(new CsvTable(schema, _resource.getName()));
      schema.setTable(new CsvTable(schema, tableName));
    }
    return schema;
  }

  // 默认以csv文件所在的父目录为schema名称 /ptone/1002/upload/test.csv
  @Override
  protected String getMainSchemaName() {
    return ResourceUtils.getParentName(_resource);
  }

  protected boolean isWritable() {
    return _writable;
  }

  private void checkWritable() {
    if (!isWritable()) {
      throw new IllegalStateException(
          "This CSV DataContext is not writable, as it based on a read-only resource.");
    }
  }

  @Override
  public void executeUpdate(UpdateScript update) {
    checkWritable();
    CsvUpdateCallback callback = new CsvUpdateCallback(this);
    synchronized (WRITE_LOCK) {
      try {
        update.run(callback);
      } finally {
        callback.close();
      }
    }
  }
}
