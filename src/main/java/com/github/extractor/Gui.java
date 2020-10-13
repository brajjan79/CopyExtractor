package com.github.extractor;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Gui extends Application {

    @Override
    public void start(final Stage primaryStage) {
        try {
            final BorderPane root = new BorderPane();

            final Line line = createLine(600.0, 0.0, 600.0, 1200.0);
            final Line line2 = createLine(0.0, 600.0, 600.0, 600.0);
            //final Text text = createText("Hi how are you", 10, 50, 25);

            final Button selectInputFolder = createButton(10.0, 50.0, "Select input folder");
            selectInputFolder.setOnAction(createSelectFolderEventHandler(primaryStage));
            final Button selectOutputFolder = createButton(140.0, 50.0, "Select output folder");
            selectOutputFolder.setOnAction(createSelectFolderEventHandler(primaryStage));

            final CheckBox check = createCheckbox("recursive", 10, 90);
            final CheckBox check2 = createCheckbox("keep folder structure", 10, 110);

            final Button startButton = createButton(10, 130, "Extract");
            final EventHandler<MouseEvent> eventHandler = clickStart(startButton);
            startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
            final Button stopButton = createButton(80, 130, "Stop");

            final Group group = new Group(check, check2, selectInputFolder, selectOutputFolder, line, line2, startButton, stopButton);

            final Scene scene = new Scene(group, 1200, 1200);
            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            scene.setFill(Color.LAVENDER);

            primaryStage.setTitle("Rar Extractor");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private CheckBox createCheckbox(final String label, final double x, final double y) {
        final CheckBox check = new CheckBox();
        check.setText(label);
        check.setLayoutX(x);
        check.setLayoutY(y);
        return check;
    }

    private EventHandler<ActionEvent> createSelectFolderEventHandler(final Stage primaryStage) {
        final DirectoryChooser fileChooser = new DirectoryChooser();
        final EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                configureFileChooser(fileChooser);
                final File file = fileChooser.showDialog(primaryStage);
                if (file != null) {
                    System.out.println(file.getAbsolutePath());
                }
            }

            private void configureFileChooser(final DirectoryChooser fileChooser) {
                    fileChooser.setTitle("Select folder");
                    //fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            }
        };
        return eventHandler;
    }

    private Button createButton(final double x, final double y, final String buttonName) {
        final Button selectOutputFolder = new Button(buttonName);
        selectOutputFolder.setLayoutX(x);
        selectOutputFolder.setLayoutY(y);
        return selectOutputFolder;
    }

    private Line createLine(final double startX, final double startY, final double endX, final double endY) {
        final Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        return line;
    }

    private Text createText(final String message, final int x, final int y, final int size) {
        final Text text = new Text();
        text.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, size));
        text.setX(x);
        text.setY(y);
        //text.setFill(Color.BROWN);
        //text.setStrokeWidth(2);
        //text.setStroke(Color.BLUE);
        text.setText(message);
        return text;
    }

    private Circle createCircle() {
        final Circle circle = new Circle();
        circle.setCenterX(300.0f);
        circle.setCenterY(135.0f);
        circle.setRadius(25.0f);
        circle.setFill(Color.BROWN);
        circle.setStrokeWidth(20);
        return circle;
    }

    private EventHandler<MouseEvent> clickStart(final Button startButton) {
        //Creating the mouse event handler
        final EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
           @Override
           public void handle(final MouseEvent e) {

              System.out.println("Hello World " + e.getButton());
              final MouseButton button = e.getButton();
              if (button == MouseButton.PRIMARY) {
                  startButton.setTextFill(Color.DARKSLATEBLUE);
              }
              if (button == MouseButton.SECONDARY) {
                  startButton.setTextFill(Color.ALICEBLUE);
              }
              if (button == MouseButton.MIDDLE) {
                  startButton.setTextFill(Color.BISQUE);
              }

           }
        };
        return eventHandler;
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
