import javafx.scene.control.*;
import javafx.event.*;

public class CwacosUI  {

    public CwacosUI(){}

    public static void startUI(String[] args){
        CwacosView.beginUI(args);
    }

    // need some type of "on start" and "on end"

    //Method adds functionality to the add ticker button
    public void addStockFunction(Menu menu, MenuItem item){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            TextInputDialog td = new TextInputDialog();
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.showAndWait();

                menu.getItems().add(new MenuItem(td.getEditor().getText().toUpperCase()));
            }
        };

        item.setOnAction(event);
    }

    //Method adds functionality to the remove ticker button
    public Button removeTickerFunction(TextInputDialog td, ComboBox<String> cb, Button btn) {
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.showAndWait();
            }
        };

        btn.setOnAction(event);

        return btn;
    }
}