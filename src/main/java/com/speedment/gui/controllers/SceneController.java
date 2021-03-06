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
package com.speedment.gui.controllers;

import com.speedment.core.code.model.java.MainGenerator;
import com.speedment.core.config.model.Project;
import com.speedment.core.config.model.aspects.Child;
import com.speedment.core.config.model.aspects.Node;
import com.speedment.gui.MainApp;
import static com.speedment.gui.MainApp.showWebsite;
import com.speedment.gui.controllers.NotificationController.Notification;
import static com.speedment.gui.controllers.NotificationController.showNotification;
import com.speedment.gui.icons.Icons;
import com.speedment.gui.icons.SilkIcons;
import com.speedment.gui.properties.TableProperty;
import com.speedment.gui.properties.TablePropertyManager;
import com.speedment.gui.properties.TablePropertyRow;
import com.speedment.gui.util.FadeAnimation;
import static com.speedment.gui.util.FadeAnimation.fadeIn;
import static com.speedment.gui.util.ProjectUtil.createOpenProjectHandler;
import static com.speedment.gui.util.ProjectUtil.createSaveAsProjectHandler;
import static com.speedment.gui.util.ProjectUtil.createSaveProjectHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static javafx.animation.Animation.INDEFINITE;
import static javafx.animation.Interpolator.EASE_BOTH;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import static javafx.util.Duration.ZERO;
import static javafx.util.Duration.millis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public class SceneController implements Initializable {
    
    private final static Logger LOGGER = LogManager.getLogger(SceneController.class);
    private final static String GITHUB_URL = "https://github.com/speedment/speedment";

    @FXML private Button buttonNew;
    @FXML private Button buttonOpen;
    @FXML private Button buttonGenerate;
    @FXML private ImageView logo;
    @FXML private TreeView<Child<?>> treeHierarchy;
    @FXML private TableView<String> tableProjectSettings;
    @FXML private VBox propertiesContainer;
    @FXML private WebView output;
    @FXML private Menu menuFile;
    @FXML private MenuItem mbNew;
    @FXML private MenuItem mbOpen;
    @FXML private MenuItem mbSave;
    @FXML private MenuItem mbSaveAs;
    @FXML private MenuItem mbQuit;
    @FXML private Menu menuEdit;
    @FXML private MenuItem mbGenerate;
    @FXML private Menu menuHelp;
    @FXML private MenuItem mbGitHub;
    @FXML private MenuItem mbAbout;
    @FXML private StackPane arrowContainer;
    @FXML private Label arrow;

    private File savedFile;
    private final Stage stage;
    private Project project;
    private TablePropertyManager propertyMgr;

    private SceneController(Stage stage, Project project) {
        this (stage, project, null);
    }
    
    private SceneController(Stage stage, Project project, File savedFile) {
        this.stage     = stage;
        this.project   = project;
        this.savedFile = savedFile;
    }

    /**
     * Initializes the controller class.
     *
     * @param url the URL to use
     * @param rb the ResourceBundle to use
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.propertyMgr = new TablePropertyManager(treeHierarchy);

        populateTree(project);
        arrow.setOpacity(0);

        mbNew.setGraphic(Icons.NEW_PROJECT.view());
        mbOpen.setGraphic(Icons.OPEN_PROJECT.view());
        mbSave.setGraphic(SilkIcons.DISK.view());
        mbSaveAs.setGraphic(SilkIcons.DISK_MULTIPLE.view());
        mbQuit.setGraphic(SilkIcons.DOOR_IN.view());
        mbGenerate.setGraphic(Icons.RUN_PROJECT.view());
        mbGitHub.setGraphic(SilkIcons.USER_COMMENT.view());
        mbAbout.setGraphic(SilkIcons.INFORMATION.view());

        buttonNew.setGraphic(Icons.NEW_PROJECT_24.view());
        buttonOpen.setGraphic(Icons.OPEN_PROJECT_24.view());
        buttonGenerate.setGraphic(Icons.RUN_PROJECT_24.view());

        // New project.
        final EventHandler<ActionEvent> newProject = ev -> {
            writeToLog("Creating new project.");
            final Stage newStage = new Stage();
            ProjectPromptController.showIn(newStage);
        };

        buttonNew.setOnAction(newProject);
        mbNew.setOnAction(newProject);

        // Open project.
        final EventHandler<ActionEvent> openProject = createOpenProjectHandler(
            stage, (f, p) -> {
                
            savedFile = f;
            treeHierarchy.setRoot(branch(p));
            project = p;
            writeToLog("Opened config file: " + savedFile);
        });

        buttonOpen.setOnAction(openProject);
        mbOpen.setOnAction(openProject);

        // Save application
        mbSave.setOnAction(createSaveProjectHandler(this, f -> {
            savedFile = f;
            writeToLog("Saved config file: " + savedFile);
        }));

        // Save application as
        mbSaveAs.setOnAction(createSaveAsProjectHandler(this, f -> {
            savedFile = f;
            writeToLog("Saved config file: " + savedFile);
        }));

        // Help
        mbGitHub.setOnAction(ev -> showWebsite(GITHUB_URL));
        logo.setOnMousePressed(ev -> showWebsite(GITHUB_URL));

        // Generate code
        final EventHandler<ActionEvent> generate = ev -> {
            outputBuffer.delete(0, outputBuffer.length());
            final Instant started = Instant.now();
            writeToLog("Generating classes " + project.getPackageName() + "." + project.getName() + ".*");
            writeToLog("Target directory is " + project.getPackageLocation());

            final MainGenerator instance = new MainGenerator();
            
            try {
                instance.accept(project);
                writeGenerationStatus(
                    started, 
                    Instant.now(), 
                    instance.getFilesCreated(), 
                    true
                );
                
                showNotification(
                    arrowContainer, 
                    "The code generation succeeded!", 
                    Notification.SUCCESS
                );
            } catch (Exception ex) {
                writeGenerationStatus(
                    started, 
                    Instant.now(), 
                    instance.getFilesCreated(), 
                    false
                );
                LOGGER.error("Error! Failed to generate code.", ex);
            }
            
            removeArrow();
        };

        buttonGenerate.setOnAction(generate);
        mbGenerate.setOnAction(generate);
		
		// About
		mbAbout.setOnAction(ev -> {
			AboutController.showIn(stage);
		});

        // Quit application
        mbQuit.setOnAction(ev -> {
            stage.close();
        });
        
        ActionChoiceController.showActionChoice(arrowContainer, 
            // onGenerate
            () -> generate.handle(null),
            
            // onConfigure
            () -> animateArrow()
        );
    }

    public Stage getStage() {
        return stage;
    }

    public Project getProject() {
        return project;
    }

    public File getLastSaved() {
        return savedFile;
    }
    
    public SceneController setLastSaved(File savedFile) {
        this.savedFile = savedFile;
        return this;
    }

    private void populateTree(Project project) {
        final ListChangeListener<? super TreeItem<Child<?>>> change = l -> {

            populatePropertyTable(
                propertyMgr.propertiesFor(
                    l.getList().stream()
                    .map(i -> i.getValue())
                    .collect(Collectors.toList())
                )
            );
        };

        treeHierarchy.setCellFactory(v -> new TreeCell<Child<?>>() {

            @Override
            protected void updateItem(Child<?> item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(iconFor(item));
                    setText(item.getName());
                }
            }
        });

        treeHierarchy.getSelectionModel().setSelectionMode(MULTIPLE);
        treeHierarchy.getSelectionModel().getSelectedItems().addListener(change);
        treeHierarchy.setRoot(branch(project));
    }

    private ImageView iconFor(Node node) {
        final Icons icon = Icons.forNodeType(node.getInterfaceMainClass());

        if (icon == null) {
            
            LOGGER.error("Internal error.", new RuntimeException(
                "Unknown node type '" + node.getInterfaceMainClass().getName() + "'."
            ));
            
            return Icons.QUESTION.view();
        } else {
            return icon.view();
        }
    }

    private TreeItem<Child<?>> branch(Child<?> node) {
        final TreeItem<Child<?>> branch = new TreeItem<>(node);
        branch.setExpanded(true);

        node.asParent().ifPresent(p -> {
            p.stream().map(c -> branch(c)).forEachOrdered(
                c -> branch.getChildren().add(c)
            );
        });

        return branch;
    }

    private void populatePropertyTable(Stream<TableProperty<?>> properties) {
        propertiesContainer.getChildren().clear();

        properties.forEachOrdered(p -> {
            final HBox row = new TablePropertyRow<>(p);
            propertiesContainer.getChildren().add(row);
        });
    }

    private void animateArrow() {
        if (arrowContainer.getChildren().contains(arrow)) {
            fadeIn(arrow);

            final DropShadow glow = new DropShadow();
            glow.setBlurType(BlurType.TWO_PASS_BOX);
            glow.setColor(Color.rgb(0, 255, 255, 1.0));
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(0.0);
            arrow.setEffect(glow);

            final KeyFrame kf0 = new KeyFrame(ZERO,
                new KeyValue(arrow.translateXProperty(), 145, EASE_BOTH),
                new KeyValue(arrow.translateYProperty(), -15, EASE_BOTH),
                new KeyValue(glow.radiusProperty(), 32, EASE_BOTH)
            );

            final KeyFrame kf1 = new KeyFrame(millis(400),
                new KeyValue(arrow.translateXProperty(), 135, EASE_BOTH),
                new KeyValue(arrow.translateYProperty(), 5, EASE_BOTH),
                new KeyValue(glow.radiusProperty(), 0, EASE_BOTH)
            );

            final Timeline tl = new Timeline(kf0, kf1);
            tl.setAutoReverse(true);
            tl.setCycleCount(INDEFINITE);
            tl.play();

            final EventHandler<MouseEvent> over = ev -> removeArrow();

            arrow.setOnMouseEntered(over);
        }
    }
    
    private void removeArrow() {
        if (arrowContainer.getChildren().contains(arrow)) {
            if (arrow.getOpacity() > 0) {
                FadeAnimation.fadeOut(arrow, e -> arrowContainer.getChildren().remove(arrow));
            } else {
                arrowContainer.getChildren().remove(arrow);
            }
        }
    }
    
    public static SceneController showIn(Stage stage, Project project) {
        final FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/Scene.fxml"));
        final SceneController control = new SceneController(stage, project);
        loader.setController(control);

        try {
            final Parent root = (Parent) loader.load();
            final Scene scene = new Scene(root);

            stage.hide();
            stage.setTitle("Speedment ORM");
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        return control;
    }

    public static SceneController showIn(Stage stage, Project project, File savedFile) {
        return Optional.ofNullable(showIn(stage, project))
            .map(sc -> sc.setLastSaved(savedFile))
            .orElse(null);
    }

    private final StringBuilder outputBuffer = new StringBuilder();
    
    private void writeToLog(String msg) {
        outputBuffer.append("<p style=\"font-family:Courier,monospace;font-size:12px;margin:0px;padding:0px;\">").append(msg).append("</p>");
        output.getEngine().loadContent(outputBuffer.toString());
        LOGGER.info(msg);
    }
    
    private void writeGenerationStatus(Instant started, Instant finished, int filesCreated, boolean succeeded) {
        
        final LocalDateTime ldt = LocalDateTime.ofInstant(finished, ZoneId.systemDefault());
        final DateTimeFormatter format = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.systemDefault());
        final String status = succeeded ? ": Generation completed! :" : "-: Generation failed! :--";
        final String color = succeeded ? "lightgreen" : "lightpink";
        
        final Duration dur = Duration.between(started, finished);
        final long durSecs = dur.getSeconds();
        final long durMils = dur.multipliedBy(1000).getSeconds() % 1000;
        String strMils = Long.toString(durMils);
        switch (strMils.length()) {
            case 1 : strMils = "00" + strMils; break;
            case 2 : strMils = "0" + strMils; break;
        }
        
        try {
            writeToLog("<pre style=\"background:" + color + ";\">" + 
                ".------------" + status + "------------." + "\n" +
                " Total time: " + durSecs + "." + strMils + "s\n" +
                " Finished at: " + format.format(finished) + "\n" +
                " Files generated: " + filesCreated + "\n" +
                "'-------------------------------------------------'</pre>"
            );
        } catch (UnsupportedTemporalTypeException ex) {
            LOGGER.error("Could not parse time correctly.", ex);
            writeToLog("<span style=\"background:lightpink;\">Time parsing failed unexpectedly.</span>");
        }
    }
}
