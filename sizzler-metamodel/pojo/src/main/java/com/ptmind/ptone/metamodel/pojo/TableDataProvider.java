package com.ptmind.ptone.metamodel.pojo;

import org.apache.metamodel.util.HasName;
import org.apache.metamodel.util.SimpleTableDef;

import java.io.Serializable;

/**
 * Created by ptmind on 2015/11/26.
 */
public interface TableDataProvider<E> extends HasName, Iterable<E>, Serializable {

  public SimpleTableDef getTableDef();

  public Object getValue(String column, E record);

}
