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

import com.speedment.core.field.BasePredicate;
import com.speedment.core.field.BinaryPredicateBuilder;
import com.speedment.core.field.StandardBinaryOperator;
import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author pemi
 * @param <ENTITY> Entity type
 * @param <V> Value type
 */
public class ReferenceBinaryPredicateBuilder<ENTITY, V extends Comparable<? super V>> extends BasePredicate<ENTITY> implements BinaryPredicateBuilder<ENTITY, V> {

    private final ReferenceField<ENTITY, V> field;
    private final V value;
    private final StandardBinaryOperator binaryOperator;
    private final Comparator<V> entityComparator;

    public ReferenceBinaryPredicateBuilder(
        final ReferenceField<ENTITY, V> field,
        final V value,
        final StandardBinaryOperator binaryOperator,
        final Comparator<V> entityComparator
    ) {
        this.field = Objects.requireNonNull(field);
        this.value = value;
        this.entityComparator = Objects.requireNonNull(entityComparator);
        this.binaryOperator = Objects.requireNonNull(binaryOperator);
    }

    public ReferenceBinaryPredicateBuilder(
        final ReferenceField<ENTITY, V> field,
        final V value,
        final StandardBinaryOperator binaryOperator
    ) {
        this(field, value, binaryOperator, Comparator.naturalOrder());
    }

    @Override
    public boolean test(final ENTITY entity) {
        return test(Objects.compare(field.getFrom(entity), getValue(), entityComparator));
    }

    public boolean test(final int compare) {
        return binaryOperator.getComparator().test(compare);
    }

    @Override
    public ReferenceField<ENTITY, V> getField() {
        return field;
    }

    public V getValue() {
        return value;
    }

    @Override
    public StandardBinaryOperator getOperator() {
        return binaryOperator;
    }

    @Override
    public V getValueAsObject() {
        return getValue();
    }

}
