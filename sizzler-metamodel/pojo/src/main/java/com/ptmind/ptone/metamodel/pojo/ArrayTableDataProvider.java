package com.ptmind.ptone.metamodel.pojo;

import org.apache.metamodel.util.SimpleTableDef;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by ptmind on 2015/11/26.
 */
public class ArrayTableDataProvider implements TableDataProvider<Object[]> {

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
