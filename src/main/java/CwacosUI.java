<<<<<<< HEAD
import javafx.scene.control.*;
import javafx.event.*;

public class CwacosUI  {

    public CwacosUI(){}
    public static void startUI(String[] args){
        CwacosView.beginUI(args);
    }
    //Method adds functionality to the add ticker button
    public Button addTickerFunction(TextInputDialog td, ComboBox<String> cb, Button btn){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.showAndWait();

                cb.getItems().add(td.getEditor().getText().toUpperCase());
            }
        };

        btn.setOnAction(event);

        return btn;
=======
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class CwacosUI extends Application {

    public static void beginUI(String[] args) {
        launch(args);
>>>>>>> 9f07506e308a020dd49f1e0c00f907ef4c8a5592
    }

    //Method adds functionality to the remove ticker button
    public Button removeTickerFunction(TextInputDialog td, ComboBox<String> cb, Button btn) {
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.showAndWait();

                cb.getItems().remove(td.getEditor().getText().toUpperCase());
            }
        };

        btn.setOnAction(event);

        return btn;
    }
}