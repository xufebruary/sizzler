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
package org.apache.metamodel.query;

/**
 * Defines the types of operators that can be used in filters.
 *
 * @see FilterItem
 */
public interface OperatorType {

  public static final OperatorType EQUALS_TO = new OperatorTypeImpl("=", false);

  public static final OperatorType DIFFERENT_FROM = new OperatorTypeImpl("<>", false);

  public static final OperatorType LIKE = new OperatorTypeImpl("LIKE", true);

  public static final OperatorType GREATER_THAN = new OperatorTypeImpl(">", false);

  public static final OperatorType GREATER_THAN_OR_EQUAL = new OperatorTypeImpl(">=", false);

  public static final OperatorType LESS_THAN = new OperatorTypeImpl("<", false);

  public static final OperatorType LESS_THAN_OR_EQUAL = new OperatorTypeImpl("<=", false);

  public static final OperatorType IN = new OperatorTypeImpl("IN", true);
  /**
   * @author shaoqiang.guo
   */
  public static final OperatorType NOT_LIKE = new OperatorTypeImpl("NOT_LIKE", true);

  public static final OperatorType START_LIKE = new OperatorTypeImpl("START_LIKE", true);

  public static final OperatorType END_LIKE = new OperatorTypeImpl("END_LIKE", true);

  public static final OperatorType NOT_START_LIKE = new OperatorTypeImpl("NOT_START_LIKE", true);

  public static final OperatorType NOT_END_LIKE = new OperatorTypeImpl("NOT_END_LIKE", true);
  /**
   * end
   */
  public static final OperatorType[] BUILT_IN_OPERATORS = new OperatorType[] {
      GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, EQUALS_TO, DIFFERENT_FROM, LIKE, GREATER_THAN,
      LESS_THAN, IN, NOT_LIKE, START_LIKE, END_LIKE, NOT_START_LIKE, NOT_END_LIKE};

/**
     * Determines if this operator requires a space delimitor. Operators that are written using letters usually require
     * space delimitation whereas sign-based operators such as "=" and "<" can be applied even without any delimitaton.
     *
     * @return
     */
  public boolean isSpaceDelimited();

  public String toSql();

}
