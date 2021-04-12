import javafx.scene.control.*;
import javafx.event.*;

public class CwacosUI  {

    public CwacosUI(){}

    public static void startUI(String[] args){
        CwacosView.beginUI(args);
    }

    // need some type of "on start" and "on end"

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