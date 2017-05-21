package com.sizzler.metamodel.pojo;

import java.util.Collection;
import java.util.Iterator;

import org.apache.metamodel.util.SimpleTableDef;

public class ArrayTableDataProvider implements TableDataProvider<Object[]> {

  private static final long serialVersionUID = -6240512529924956208L;

  private SimpleTableDef tableDef;
  private Collection<Object[]> dataCollection;

  public ArrayTableDataProvider(SimpleTableDef tableDef, Collection<Object[]> dataCollection) {
    this.tableDef = tableDef;
    this.dataCollection = dataCollection;
  }

  @Override
  public SimpleTableDef getTableDef() {
    return tableDef;
  }

  @Override
  public Object getValue(String column, Object[] record) {
    int index = tableDef.indexOf(column);
    return record[index];
  }

  @Override
  public String getName() {
    return getTableDef().getName();
  }

  @Override
  public Iterator<Object[]> iterator() {
    return dataCollection.iterator();
  }
}
