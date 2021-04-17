import javafx.scene.control.*;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import java.lang.Exception;

public class CwacosUI  {

    public CwacosUI(){}

    public static void startUI(String[] args){
        CwacosView.beginUI(args);
    }

    // need some type of "on start" and "on end"

    //Method adds functionality to the add stock button
    public MenuItem addActionFunction(MenuItem item, TextInputDialog choiceWindow, MenuBar favorites){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                GridPane gp = (GridPane)choiceWindow.getDialogPane().getContent();
                gp.getChildren().set(2, addButtonFunction((Button)gp.getChildren().get(2), favorites, choiceWindow));
                // show the text input dialog
                choiceWindow.showAndWait();
            }
        };

        item.setOnAction(event);

        return item;
    }

    //Method adds functionality to the remove stock button
    public MenuItem removeActionFunction(MenuItem btn, TextInputDialog choiceWindow, MenuBar favorites) {
        //TextInputDialog td = new TextInputDialog(); //Text dialog window asks the user to enter a ticker
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {   
                GridPane gp = (GridPane)choiceWindow.getDialogPane().getContent();
                gp.getChildren().set(2, removeButtonFunction((Button)gp.getChildren().get(2), favorites, choiceWindow));
                // show the text input dialog
                choiceWindow.showAndWait();
            }
        };

        btn.setOnAction(event);

        return btn;
    }

    //This method adds functionality to the add button in the dialog window.
    private Button addButtonFunction(Button btn, MenuBar mb, TextInputDialog choiceWindow){
        //Store gridpane to reference the content in it (javafx is dumb and you need to do this).
        GridPane gp = (GridPane)choiceWindow.getDialogPane().getContent();
        //Reset text field box when the window opens.
        ((TextField)gp.getChildren().get(1)).setText("");


        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                int count = 0;
                //Get the ticker the user inputted.
                TextField input = (TextField)gp.getChildren().get(1);
                //Try/catch to catch exceptions.
                try {
                    //Store the string value of the ticker the user inputted.
                    String text = input.getText();

                    //Store the ticker type the user selected.
                    String typeSelection = ((ComboBox<String>)gp.getChildren().get(0)).getValue();

                    int typeInt;
                    //Insert a menu item for the ticker depending on ticker type selection.
                    if (typeSelection.contains("Stocks")) {
                        mb.getMenus().get(1).getItems().add(new MenuItem(text.toUpperCase()));
                        typeInt = 0;
                    } else {
                        mb.getMenus().get(2).getItems().add(new MenuItem(text.toUpperCase()));
                        typeInt = 1;
                    }

                    String addFavoriteStatus = CwacosData.addFavorite(text, typeInt);

                    System.out.println(addFavoriteStatus);

                    //statusOutput.setText(addFavoriteStatus);
                    CwacosData.setActiveData(text);

                    // update table view to be populated with the data from the data in the map.
                    // for all intents and purposes this should create an empty table.

                } catch (Exception exception) {
                    System.out.println("pain " + exception);
                }
                // close the window
                choiceWindow.close();
            }
        };

        btn.setOnAction(event);

        return btn;
    }

    //This method adds functionality to the remove button in the dialog window
    private Button removeButtonFunction(Button btn, MenuBar mb, TextInputDialog choiceWindow){
        //Store gridpane to reference the content in it (javafx is dumb and you need to do this).
        GridPane gp = (GridPane)choiceWindow.getDialogPane().getContent();
        //Reset text field box when the window opens.
        ((TextField)gp.getChildren().get(1)).setText("");

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                //Get the ticker the user inputted
                TextField input = (TextField)gp.getChildren().get(1);
                String ticker = input.getText().toUpperCase();
                //Get the menu the user selected
                String typeSelection = ((ComboBox<String>)gp.getChildren().get(0)).getValue();
                //Pick one of the ticker menus depending on ticker type selection
                Menu menu = new Menu();
                if(typeSelection.contains("Stocks"))
                    menu = mb.getMenus().get(1);
                else
                    menu = mb.getMenus().get(2);

                //Search the menu for the ticker I want to remove
                for(int i = 0; i < menu.getItems().size(); i++) {
                    MenuItem current = menu.getItems().get(i);  //Store the current menu item that's being checked 
                    String text = current.getText();    //Store the string contained in the menu item
                    //Check if the ticker the user entered matches the current menu item
                    if (text.contains(ticker))
                        menu.getItems().remove(current);    //Remove that ticker from the menu
                }
                //menu.getItems().remove(menu.getItems().indexOf(ticker));

                // close the window
                choiceWindow.close();
            }
        };

        btn.setOnAction(event);

        return btn;
    }
}