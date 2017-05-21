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
package org.apache.metamodel.data;

import java.io.Serializable;

import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;

/**
 * Represents the header of a {@link DataSet}, which define the columns/SelectItems of it.
 */
public interface DataSetHeader extends Serializable {

  public SelectItem[] getSelectItems();

  public int size();

  public int indexOf(SelectItem item);

  public int indexOf(Column column);

  public SelectItem getSelectItem(int i);
}
