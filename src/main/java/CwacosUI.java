import javafx.scene.control.*;
import javafx.event.*;

public class CwacosUI  {

    public CwacosUI(){}

    public static void startUI(String[] args){
        CwacosView.beginUI(args);
    }

    // need some type of "on start" and "on end"

    //Method adds functionality to the add stock button
    public MenuItem addStockFunction(Menu menu, MenuItem item){
        TextInputDialog td = new TextInputDialog();
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.showAndWait();
                MenuItem ticker = new MenuItem(td.getEditor().getText().toUpperCase());
                menu.getItems().add(ticker);
            }
        };

        item.setOnAction(event);

        return item;
    }

    //Method adds functionality to the remove stock button
    public MenuItem removeStockFunction(Menu menu, MenuItem btn) {
        TextInputDialog td = new TextInputDialog();
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {   
                // show the text input dialog
                td.showAndWait();
                MenuItem ticker = new MenuItem(td.getEditor().getText().toUpperCase()); //Ticker the user inputted
                //Search Stocks menu for the ticker I want to remove
                for(int i = 0; i < menu.getItems().size(); i++) {
                    MenuItem current = menu.getItems().get(i);  //Store the current searched 
                    String text = current.getText();
                    boolean test = (text.contains(ticker.getText()));
                    System.out.println(test);
                    if (test)
                        menu.getItems().remove(current);
                }
                //menu.getItems().remove(menu.getItems().indexOf(ticker));
            }
        };

        btn.setOnAction(event);

        return btn;
    }

    //Method adds functionality to the add crypto button
    public MenuItem addCryptoFunction(Menu menu, MenuItem item){
        TextInputDialog td = new TextInputDialog();
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.showAndWait();

                menu.getItems().add(new MenuItem(td.getEditor().getText().toUpperCase()));
            }
        };

        item.setOnAction(event);

        return item;
    }

    //Method removes functionality to the remove crypto button
    public MenuItem removeCryptoFunction(Menu menu, MenuItem item){
        TextInputDialog td = new TextInputDialog();
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.showAndWait();

                menu.getItems().remove(new MenuItem(td.getEditor().getText().toUpperCase()));

            }
        };

        item.setOnAction(event);

        return item;
    }
}