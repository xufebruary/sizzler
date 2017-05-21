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
package org.apache.metamodel.util;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.apache.metamodel.query.FilterItem;
import org.apache.metamodel.query.OperatorType;

/**
 * Represents a pattern with a wildcard character. These are typically used in FilterItems with the
 * LIKE operator.
 *
 * @see FilterItem
 */
public final class WildcardPattern implements Serializable {

  private static final long serialVersionUID = 857462137797209624L;
  private String _pattern;
  private char _wildcard;
  private boolean _endsWithDelim;

  public WildcardPattern(String pattern, char wildcard) {
    _pattern = pattern;
    _wildcard = wildcard;
    _endsWithDelim = (_pattern.charAt(pattern.length() - 1) == _wildcard);
  }

  public boolean matches(String value) {
    if (value == null) {
      return false;
    }
    StringTokenizer st = new StringTokenizer(_pattern, Character.toString(_wildcard));
    int charIndex = 0;
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      charIndex = value.indexOf(token, charIndex);
      if (charIndex == -1) {
        return false;
      }
      charIndex = charIndex + token.length();
    }
    if (!_endsWithDelim) {
      // Unless the last char of the pattern is a wildcard, we need to
      // have reached the end of the string
      if (charIndex != value.length()) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param value
   * @param operator
   * @return
   * @description not like, start like, end like, not start like, not end like
   * @author：shaoqiang.guo
   * @data：2016年8月16日 下午12:22:47
   */
  public boolean customizeMatches(String value, OperatorType operator) {
    if (value == null) {
      return false;
    }
    StringTokenizer st = new StringTokenizer(_pattern, Character.toString(_wildcard));
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (operator == OperatorType.NOT_LIKE) {
        return notLikeMatches(value, token);
      }
      if (operator == OperatorType.START_LIKE || operator == OperatorType.NOT_START_LIKE) {
        return startMatches(value, token, operator);
      }
      if (operator == OperatorType.END_LIKE || operator == OperatorType.NOT_END_LIKE) {
        return endMatches(value, token, operator);
      }
    }
    return false;
  }

  /**
   * @param value
   * @param token
   * @return
   * @description not like
   * @author：shaoqiang.guo
   * @data：2016年8月16日 下午12:29:04
   */
  public boolean notLikeMatches(String value, String token) {
    if (!value.contains(token)) {
      return true;
    }
    return false;
  }

  /**
   * @param value
   * @param token
   * @return
   * @description start like
   * @author：shaoqiang.guo
   * @data：2016年8月16日 下午12:29:04
   */
  public boolean startMatches(String value, String token, OperatorType operator) {
    boolean isStartWith = value.startsWith(token);
    if (operator == OperatorType.START_LIKE) {
      return isStartWith;
    }
    return !isStartWith;
  }

  /**
   * @param value
   * @param token
   * @return
   * @description start like
   * @author：shaoqiang.guo
   * @data：2016年8月16日 下午12:29:04
   */
  public boolean endMatches(String value, String token, OperatorType operator) {
    boolean isEndsWith = value.endsWith(token);
    if (operator == OperatorType.END_LIKE) {
      return isEndsWith;
    }
    return !isEndsWith;
  }
}
