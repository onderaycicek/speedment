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
package com.speedment.core.field.ints;

import com.speedment.core.field.BasePredicate;
import com.speedment.core.field.StandardUnaryOperator;
import com.speedment.core.field.UnaryPredicateBuilder;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author pemi
 * @param <ENTITY> Entity type
 */
public class IntUnaryPredicateBuilder<ENTITY> extends BasePredicate<ENTITY> implements Predicate<ENTITY>, UnaryPredicateBuilder<ENTITY> {

    private final IntField<ENTITY> field;
    private final StandardUnaryOperator unaryOperator;

    public IntUnaryPredicateBuilder(
        final IntField<ENTITY> field,
        final StandardUnaryOperator unaryOperator
    ) {
        this.field = Objects.requireNonNull(field);
        this.unaryOperator = Objects.requireNonNull(unaryOperator);
    }

    @Override
    public boolean test(ENTITY t) {
        return unaryOperator.getComparator().test(getField().getFrom(t));
    }

    @Override
    public IntField<ENTITY> getField() {
        return field;
    }

    @Override
    public StandardUnaryOperator getOperator() {
        return unaryOperator;
    }

}
