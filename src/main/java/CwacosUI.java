import javafx.scene.control.*;
import javafx.event.*;

public class CwacosUI  {

    public CwacosUI(){}

    public static void startUI(String[] args){
        CwacosView.beginUI(args);
    }

    // need some type of "on start" and "on end"

    //Method adds functionality to the add stock button
    public MenuItem addActionFunction(MenuItem item, TextInputDialog choiceWindow){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                choiceWindow.showAndWait();
            }
        };

        item.setOnAction(event);

        return item;
    }

    //Method adds functionality to the remove stock button
    public MenuItem removeActionFunction(MenuItem btn, TextInputDialog choiceWindow) {
        //TextInputDialog td = new TextInputDialog(); //Text dialog window asks the user to enter a ticker
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {   
                // show the text input dialog
                choiceWindow.showAndWait();
                /*
                MenuItem ticker = new MenuItem(td.getEditor().getText().toUpperCase()); //Ticker the user inputted
                //Search Stocks menu for the ticker I want to remove
                for(int i = 0; i < menu.getItems().size(); i++) {
                    MenuItem current = menu.getItems().get(i);  //Store the current menu item that's being checked 
                    String text = current.getText();    //Store the string contained in the menu item
                    //Check if the ticker the user entered matches the current menu item
                    if (text.contains(ticker.getText()))
                        menu.getItems().remove(current);    //Remove that ticker from the menu
                }
                //menu.getItems().remove(menu.getItems().indexOf(ticker));
                */
            }
        };

        btn.setOnAction(event);

        return btn;
    }

    public Button addButtonFunction(Button btn, Menu menu, TextInputDialog choiceWindow){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // close the window
                choiceWindow.close();
            }
        };

        btn.setOnAction(event);

        return btn;
    }

    public Button removeButtonFunction(Button btn, Menu menu, Dialog<String> choiceWindow){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // close the window
                choiceWindow.close();
                System.out.println("pressed");
            }
        };

        btn.setOnAction(event);

        return btn;
    }
}