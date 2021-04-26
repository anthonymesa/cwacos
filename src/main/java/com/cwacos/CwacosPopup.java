package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: CwacosPopup defines a custom popup window that can be run from within CwacosView.
 *      Where there are other ways of providing dialogue boxes, a custom dialoge was created for
 *      a couple of helpful reasons:
 * 
 *      - Pass/Fail buttons can be set with customization, or ommitted (i.e. "Okay", "Cancel");
 *      - Functions can be defined for "Okay" button onclick event that return with pass/fail, 
 *          allowing improper dialog inputs to be handled accordingly.
 *      - Call overloading, based on whether a single button or two buttons are necessary. This
 *          could also extend to no buttons, etc.
 * 
 * Contributing Authors:
 *      Jack fink
 *      Anthony Mesa
 */

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.util.function.Function;

/**
 * custom static popup class that we designed so as not
 * to have to mess with the dialoguebox included in javafx
 */
public class CwacosPopup {

    private static final int DIALOGUE_WIDTH = 500;

    // Window to draw.
    private static final Stage window = new Stage();
    private static boolean isInit = false;

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
                int success = (int)func.apply(null);

                if (success == 0){
                    window.close();
                }
            }
        };
    }

    /**
     * Coming soon...
     *
     * @param _title display title of popup window
     * @param _content HBox of javafx contents to display in dialogue window
     * @param _func function to run when okay is clicked.
     */
    public static void display(String _title, String _okayLabel, String _cancel_label, Pane _content, Function<Object, Object> _func) {

        if(!isInit){
            System.out.println("CwacosPopup must be initialized before use");
            return;
        }

        // Block user inputs on non-pupup windows
        window.setTitle(_title);
        window.setMinWidth(DIALOGUE_WIDTH);

        VBox windowContents = new VBox();
        Pane changingContent = _content;
        windowContents.getChildren().add(changingContent);

        Button successBtn = new Button(_okayLabel);
        successBtn.setOnAction(setOperation(_func));

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
