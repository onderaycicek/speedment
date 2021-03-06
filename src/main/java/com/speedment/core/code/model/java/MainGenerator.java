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
package com.speedment.core.code.model.java;

import com.speedment.codegen.util.Formatting;
import com.speedment.codegen.base.Generator;
import com.speedment.codegen.base.Meta;
import com.speedment.codegen.java.JavaGenerator;
import com.speedment.codegen.lang.models.File;
import com.speedment.core.code.model.Translator;
import com.speedment.core.code.model.java.entity.EntityBuilderTranslator;
import com.speedment.core.code.model.java.entity.EntityImplTranslator;
import com.speedment.core.code.model.java.manager.EntityManagerImplTranslator;
import com.speedment.core.code.model.java.manager.EntityManagerTranslator;
import com.speedment.core.code.model.java.entity.EntityTranslator;
import com.speedment.core.code.model.java.lifecycle.SpeedmentApplicationMetadataTranslator;
import com.speedment.core.code.model.java.lifecycle.SpeedmentApplicationTranslator;
import com.speedment.core.config.model.Project;
import com.speedment.core.config.model.Table;
import com.speedment.util.analytics.AnalyticsUtil;
import static com.speedment.util.analytics.FocusPoint.GENERATE;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author pemi
 */
public class MainGenerator implements Consumer<Project> {

    private final static Logger LOGGER = LogManager.getLogger(MainGenerator.class);
    private int fileCounter = 0;

    @Override
    public void accept(Project project) {
		AnalyticsUtil.notify(GENERATE);
        fileCounter = 0;
		
        final List<Translator<?, File>> translators = new ArrayList<>();

        final Generator gen = new JavaGenerator(
            new SpeedmentTransformFactory()
        );

        translators.add(new SpeedmentApplicationTranslator(gen, project));
        translators.add(new SpeedmentApplicationMetadataTranslator(gen, project));
        
        project.traversalOf(Table.class).forEach(table -> {
            translators.add(new EntityTranslator(gen, table));
            translators.add(new EntityBuilderTranslator(gen, table));
            translators.add(new EntityImplTranslator(gen, table));
            translators.add(new EntityManagerTranslator(gen, table));
            translators.add(new EntityManagerImplTranslator(gen, table));
        });

        Formatting.tab("    ");
        gen.metaOn(translators.stream()
            .map(t -> t.get())
            .collect(Collectors.toList()))
            .forEach(c -> {
                
                final String fname = project.getPackageLocation()
                + "/"
                + c.getModel().getName();
                final String content = c.getResult();
                final Path path = Paths.get(fname);
                path.getParent().toFile().mkdirs();

                try {
                    Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    fileCounter++;
                } catch (IOException ex) {
                    LOGGER.error("Failed to create file " + fname, ex);
                }

                LOGGER.info("done");

                System.out.println("*** BEGIN File:" + fname);
                System.out.println(content);
                System.out.println("*** END   File:" + fname);
            });
        
        
        List<Table> tables = project
            .traversalOf(Table.class)
            .collect(toList());
        
        gen.metaOn(tables, File.class).forEach(meta -> {
            writeToFile(project, gen, meta);
            fileCounter++;
        });
        
        
//        
//        
//        translators.forEach(t -> {
//            File file = t.get();
//            final Optional<String> code = cg.on(file);
//            System.out.println("*** BEGIN File:" + file.getName());
//            System.out.println(code.get());
//            System.out.println("*** END   File:" + file.getName());
//        });

    }
    
    public int getFilesCreated() {
        return fileCounter;
    }
    
    private static void writeToFile(Project project, Generator gen, Meta<Table, File> c) {
        final Optional<String> content = gen.on(c.getResult());
        
        if (content.isPresent()) {
            final String fname = 
                project.getPackageLocation() + 
                "/" + c.getResult().getName();
            
            final Path path = Paths.get(fname);
            path.getParent().toFile().mkdirs();
            
            try {
                Files.write(path, 
                    content.get().getBytes(StandardCharsets.UTF_8)
                );
            } catch (IOException ex) {
                LOGGER.error("Failed to create file " + fname, ex);
            }
            
            LOGGER.info("done");
            
            System.out.println("*** BEGIN File:" + fname);
            System.out.println(content);
            System.out.println("*** END   File:" + fname);
        } else {
            throw new IllegalArgumentException("Input file could not be generated.");
        }
        
//        final String fname = project.getPacketLocation()
//        + "/"
//        + c.getModel().getName();
//        final String content = c.getResult();
//        final Path path = Paths.get(fname);
//        path.getParent().toFile().mkdirs();
//
//        try {
//            Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//        } catch (IOException ex) {
//            LOGGER.error("Failed to create file " + fname, ex);
//        }
//
//        LOGGER.info("done");
//
//        System.out.println("*** BEGIN File:" + fname);
//        System.out.println(content);
//        System.out.println("*** END   File:" + fname);
    }

}
