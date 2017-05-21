package com.sizzler.provider.common;

import java.io.Serializable;

import org.apache.metamodel.schema.MutableSchema;

/**
 * 如果需要Schema对象，则需要实现该接口
 */
public interface Schemable extends Serializable {

  public MutableSchema getSchema();

}
