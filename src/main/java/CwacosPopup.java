
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.function.Function;

import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

/**
 * custom static popup class that we designed so as not
 * to have to mess with the dialoguebox included in javafx
 */
public class CwacosPopup {

    // Window to draw.
    public static final Stage window = new Stage();
    public static boolean isInit = false;

    // Initialize always has to be run before
    public static void init(){
        window.initModality(Modality.APPLICATION_MODAL);
        isInit = true;
    }

    /**
     * This function allows us to send in our own functionality for what the
     * success okay button should do, while also ensuring that it will always
     * close the window that it is in.
     *
     * @param func predefined functionality
     * @return an EventHandler that can be used to apply to a javafx button
     */
    public static EventHandler<ActionEvent> setOperation(Function<Object, Object> func){
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                func.apply(null);
                window.close();
            }
        };
    }

    /**
     * Coming soon...
     *
     * @param title display title of popup window
     * @param _content HBox of javafx contents to display in dialogue window
     * @param func function to run when okay is clicked.
     */
    public static void display(String title, String _okayLabel, String _cancel_label, Pane _content, Function<Object, Object> func) {

        if(!isInit){
            System.out.println("CwacosPopup must be initialized before use");
            return;
        }

        // Block user inputs on non-pupup windows
        window.setTitle(title);
        window.setMinWidth(500);

        VBox windowContents = new VBox();
        Pane changingContent = _content;
        windowContents.getChildren().add(changingContent);

        Button successBtn = new Button(_okayLabel);
        successBtn.setOnAction(setOperation(func));
        windowContents.getChildren().add(successBtn);

        Button cancelBtn = new Button(_cancel_label);
        cancelBtn.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    window.close();
                }
            }
        );
        windowContents.getChildren().add(cancelBtn);

        Scene scene = new Scene(windowContents);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Coming soon...
     *
     * @param title display title of popup window
     * @param _content HBox of javafx contents to display in dialogue window
     */
    public static void display(String title, String _cancel_label, Pane _content) {

        if(!isInit){
            System.out.println("CwacosPopup must be initialized before use");
            return;
        }

        // Block user inputs on non-pupup windows
        window.setTitle(title);
        window.setMinWidth(500);

        VBox windowContents = new VBox();
        Pane changingContent = _content;
        windowContents.getChildren().add(changingContent);

        Button cancelBtn = new Button(_cancel_label);
        cancelBtn.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        window.close();
                    }
                }
        );
        windowContents.getChildren().add(cancelBtn);

        Scene scene = new Scene(windowContents);
        window.setScene(scene);
        window.showAndWait();
    }
}
