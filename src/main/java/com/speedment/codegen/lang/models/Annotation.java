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
package com.speedment.codegen.lang.models;

import com.speedment.codegen.lang.interfaces.HasAnnotationUsage;
import com.speedment.codegen.lang.interfaces.Copyable;
import com.speedment.codegen.lang.interfaces.HasImports;
import com.speedment.codegen.lang.interfaces.HasJavadoc;
import com.speedment.codegen.lang.interfaces.HasFields;
import com.speedment.codegen.lang.interfaces.HasName;
import com.speedment.codegen.lang.models.implementation.AnnotationImpl;
import com.speedment.codegen.lang.models.modifiers.AnnotationModifier;
import java.util.function.Supplier;

/**
 *
 * @author Emil Forslund
 */
public interface Annotation extends Copyable<Annotation>, HasName<Annotation>, 
    HasJavadoc<Annotation>, HasFields<Annotation>, HasImports<Annotation>, 
    AnnotationModifier<Annotation>, HasAnnotationUsage<Annotation> {
    
    enum Factory { INST;
        private Supplier<Annotation> supplier = () -> new AnnotationImpl(null);
    }

    static Annotation of(String name) {
        return Factory.INST.supplier.get().setName(name);
    }
    
    static void setSupplier(Supplier<Annotation> a) {
        Factory.INST.supplier = a;
    }
}