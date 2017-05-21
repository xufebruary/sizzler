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
package org.apache.metamodel.query.builder;

import java.util.List;

import org.apache.metamodel.query.Query;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;

final class ColumnSelectBuilderImpl extends SatisfiedSelectBuilderImpl implements
    ColumnSelectBuilder<GroupedQueryBuilder> {

  private SelectItem selectItem;

  public ColumnSelectBuilderImpl(Column column, Query query, GroupedQueryBuilder queryBuilder) {
    super(queryBuilder);
    this.selectItem = new SelectItem(column);

    query.select(selectItem);
  }

  @Override
  public GroupedQueryBuilder as(String alias) {
    if (alias == null) {
      throw new IllegalArgumentException("alias cannot be null");
    }
    selectItem.setAlias(alias);
    return getQueryBuilder();
  }

  @Override
  protected void decorateIdentity(List<Object> identifiers) {
    super.decorateIdentity(identifiers);
    identifiers.add(selectItem);
  }
}
