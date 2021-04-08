/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CwacosUI extends Application {

    public static void beginUI(String[] _args) {
        launch(_args);
    }

    @Override
    public void start(Stage _primaryStage) {
        _primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        _primaryStage.setScene(new Scene(root, 300, 250));
        _primaryStage.show();
    }
}