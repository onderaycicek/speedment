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
package com.speedment.core.config.model.impl;

import com.speedment.core.config.model.Project;
import com.speedment.core.config.model.ProjectManager;
import com.speedment.core.config.model.aspects.Parent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author pemi
 */
public class ProjectImpl extends AbstractNamedConfigEntity implements Project {

    private ProjectManager parent;
    private final ChildHolder children;
    private String packageName, packageLocation;
    private Path configPath;

    public ProjectImpl() {
        this.children = new ChildHolder();
    }

    @Override
    protected void setDefaults() {
        setPackageLocation("src/main/java");
        setPackageName("com.company.speedment.test");
        //setConfigPath(Paths.get("src/main/groovy/speedment.groovy"));
        setConfigPath(Paths.get("src/main/groovy/speedment.groovy"));
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName(String packetName) {
        this.packageName = Objects.requireNonNull(packetName).toLowerCase();
    }

    @Override
    public String getPackageLocation() {
        return packageLocation;
    }

    @Override
    public void setPackageLocation(String packetLocation) {
        this.packageLocation = Objects.requireNonNull(packetLocation);
    }

    @Override
    public ChildHolder getChildren() {
        return children;
    }

    @Override
    public void setParentTo(Parent<?> parent) {
        setParentHelper(parent, ProjectManager.class)
                .ifPresent(p -> this.parent = p);
    }

    @Override
    public Optional<ProjectManager> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public Optional<Path> getConfigPath() {
        return Optional.ofNullable(configPath);
    }

    @Override
    public void setConfigPath(Path configPath) {
        this.configPath = configPath;
    }
}
