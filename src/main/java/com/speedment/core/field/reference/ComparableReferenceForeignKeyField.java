/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.core.field.reference;

import com.speedment.core.config.model.Column;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author pemi
 * @param <ENTITY> The entity type
 * @param <V> The value type
 * @param <FK> The foreign entity type
 */
public class ComparableReferenceForeignKeyField<ENTITY, V extends Comparable<? super V>, FK> extends ComparableReferenceField<ENTITY, V> {
	
	private final Function<ENTITY, FK> finder;

    public ComparableReferenceForeignKeyField(Supplier<Column> columnSupplier, Function<ENTITY, V> getter, Function<ENTITY, FK> finder) {
        super(columnSupplier, getter);
		this.finder = finder;
    }

    public FK findFrom(ENTITY entity) {
        return finder.apply(entity);
    }
}