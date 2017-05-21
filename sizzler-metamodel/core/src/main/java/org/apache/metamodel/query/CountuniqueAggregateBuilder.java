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

import java.util.HashSet;
import java.util.Set;

import org.apache.metamodel.ptutil.CollectionUtil;
import org.apache.metamodel.util.AggregateBuilder;

/**
 * COUNTUNIQUE函数 COUNTUNIQUE：没有重复数据的count
 *
 * @author shaoqiang.guo
 */
final class CountuniqueAggregateBuilder implements AggregateBuilder<Integer> {

  private Set<Object> set;

  // 计算非重复值得数量
  @Override
  public void add(Object o) {
    if (CollectionUtil.isEmpty(set)) {
      set = new HashSet<Object>();
    }
    if (o != null && !"".equals(o) && !"null".equalsIgnoreCase(String.valueOf(o))) {
      set.add(String.valueOf(o));
    }
  }

  @Override
  public Integer getAggregate() {
    if (CollectionUtil.isEmpty(set)) {
      set = new HashSet<Object>();
    }
    return set.size();
  }

}
