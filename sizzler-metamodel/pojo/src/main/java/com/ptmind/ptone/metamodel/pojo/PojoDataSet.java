package com.ptmind.ptone.metamodel.pojo;

import org.apache.metamodel.data.AbstractDataSet;
import org.apache.metamodel.data.DefaultRow;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;

import java.util.Iterator;

/**
 * Created by ptmind on 2015/11/26.
 */
public class PojoDataSet<E> extends AbstractDataSet {

  private TableDataProvider tableDataProvider;
  private Iterator<E> iterator;
  private E next;

  public PojoDataSet(TableDataProvider tableDataProvider, Column[] columns) {
    super(columns);
    this.tableDataProvider = tableDataProvider;
    this.iterator = tableDataProvider.iterator();
  }


  @Override
  public boolean next() {
    if (iterator.hasNext()) {
      next = iterator.next();
      return true;
    } else {
      next = null;
      return false;
    }

  }

  @Override
  public Row getRow() {
    int size = getHeader().size();
    Object[] values = new Object[size];
    for (int i = 0; i < values.length; i++) {
      SelectItem selectItem = getHeader().getSelectItem(i);
      String columnName = selectItem.getColumn().getName();
      values[i] = tableDataProvider.getValue(columnName, next);
    }
    return new DefaultRow(getHeader(), values);
  }
}
