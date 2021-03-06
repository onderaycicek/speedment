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
package com.speedment.core.code.model.java.lifecycle;

import com.speedment.codegen.base.Generator;
import com.speedment.codegen.lang.models.Class;
import com.speedment.codegen.lang.models.Constructor;
import com.speedment.codegen.lang.models.File;
import com.speedment.codegen.lang.models.Import;
import com.speedment.codegen.lang.models.Method;
import com.speedment.codegen.lang.models.Type;
import static com.speedment.codegen.lang.models.constants.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.codegen.lang.models.constants.DefaultType.VOID;
import com.speedment.codegen.lang.models.implementation.GenericImpl;
import com.speedment.core.code.model.java.DefaultJavaClassTranslator;
import static com.speedment.core.code.model.java.lifecycle.SpeedmentApplicationMetadataTranslator.METADATA;
import com.speedment.core.code.model.java.manager.EntityManagerImplTranslator;
import com.speedment.core.config.model.Project;
import com.speedment.core.config.model.Table;
import com.speedment.core.runtime.SpeedmentApplicationLifecycle;
import com.speedment.util.Paths;

/**
 *
 * @author pemi
 */
public class SpeedmentApplicationTranslator extends DefaultJavaClassTranslator<Project, Class> {

    private final String className = typeName(project()) + "Application";

    public SpeedmentApplicationTranslator(Generator cg, Project configEntity) {
        super(cg, configEntity);
    }

    @Override
    protected Class make(File file) {
        final Method onInit = Method.of("onInit", VOID)
                .protected_()
                .add(OVERRIDE)
                .add("loadAndSetProject();");

        project().traversalOf(Table.class).forEachOrdered(t -> {
            EntityManagerImplTranslator entityManagerImplTranslator = new EntityManagerImplTranslator(getCodeGenerator(), t);
            final Type managerType = entityManagerImplTranslator.getImplType();
            file.add(Import.of(managerType));
            onInit.add("put(new " + managerType.getName() + "());");
        });

        onInit.add("super.onInit();");

        //final Path path = project().getConfigPath();
        return Class.of(className)
                .public_()
                .setSupertype(Type.of(SpeedmentApplicationLifecycle.class).add(new GenericImpl(className)))
                .add(Constructor.of()
                        .public_()
                        .add("setSpeedmentApplicationMetadata(new " + className + METADATA + "());")
                )
                .add(onInit);
    }

    @Override
    protected String getJavadocRepresentText() {
        return "A Speedment Application Lifecycle";
    }

    @Override
    protected String getFileName() {
        return className;
    }
}