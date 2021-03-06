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
package com.speedment.codegen.java.views;

import static com.speedment.codegen.util.Formatting.COMMA_SPACE;
import static com.speedment.codegen.util.Formatting.EMPTY;
import static com.speedment.codegen.util.Formatting.PE;
import static com.speedment.codegen.util.Formatting.PS;
import static com.speedment.codegen.util.Formatting.SC;
import static com.speedment.codegen.util.Formatting.SPACE;
import static com.speedment.codegen.util.Formatting.block;
import static com.speedment.codegen.util.Formatting.ifelse;
import static com.speedment.codegen.util.Formatting.nl;
import com.speedment.codegen.base.Generator;
import com.speedment.codegen.base.Transform;
import com.speedment.codegen.lang.models.InterfaceMethod;
import static com.speedment.codegen.lang.models.modifiers.Modifier.*;
import com.speedment.codegen.util.CodeCombiner;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Emil Forslund
 */
public class InterfaceMethodView implements Transform<InterfaceMethod, String> {
    
    private final static String THROWS = " throws ";
    
	@Override
	public Optional<String> transform(Generator cg, InterfaceMethod model) {
		return Optional.of(ifelse(cg.on(model.getJavadoc()), s -> s + nl(), EMPTY) +
            
            cg.onEach(model.getAnnotations()).collect(CodeCombiner.joinIfNotEmpty(nl(), EMPTY, nl())) +
					
			// The only modifiers allowed are default and static
			(model.getModifiers().contains(DEFAULT) ? cg.on(DEFAULT).orElse(EMPTY) + SPACE : EMPTY) +
			(model.getModifiers().contains(STATIC) ? cg.on(STATIC).orElse(EMPTY) + SPACE : EMPTY) +
			
			cg.on(model.getType()).orElse(EMPTY) + SPACE +
			model.getName() +
			cg.onEach(model.getFields()).collect(
				Collectors.joining(COMMA_SPACE, PS, PE)
			) +
            
            cg.onEach(model.getExceptions()).collect(CodeCombiner.joinIfNotEmpty(COMMA_SPACE, THROWS, EMPTY)) +
					
			// Append body only if it is either default or static.
			(model.getModifiers().contains(DEFAULT) 
			|| model.getModifiers().contains(STATIC) ?
			SPACE + block(
				model.getCode().stream().collect(
					Collectors.joining(nl())
				)
			) : SC)
		);
	}
}
