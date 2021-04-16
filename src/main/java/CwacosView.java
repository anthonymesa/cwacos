/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.util.ArrayList;

import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

import java.lang.Math;

public class CwacosView extends Application {

    //Color palette
    private final String primary = "1D1D1D";
    private final String secondary = "4E4E4E";
    private final String white = "FFFFFF";
    private final String accent = "BB86FC";

    //Window dimensions
    private final int WINDOWWIDTH = 1280;
    private final int WINDOWHEIGHT = 720;

    CwacosUI UI = new CwacosUI();

    //Parent layout
    private GridPane root = new GridPane();

    public static void beginUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cwacos");

        //Call methods to add buttons to gridpane
        createTopBar();
        createActiveTickerViewBox();
        createFavoritesMenu();
        createCandleStickGraph();
        createBottomBar();

        setupGridPane();
        primaryStage.setScene(new Scene(root, WINDOWWIDTH, WINDOWHEIGHT));
        primaryStage.show();
    }

    //Fucntion sets styling of the gridpane and performs other necessary operations.
    private void setupGridPane() {
        root.setStyle("-fx-background-color: #" + primary + ";");   //Background color
        //Set grid spacing
        root.getColumnConstraints().addAll(createColumnConstraints());
        root.getRowConstraints().addAll(createRowConstraints());
    }

    //Method adds column constraints for however many columns there are.
    private ArrayList<ColumnConstraints> createColumnConstraints() {
        ArrayList<ColumnConstraints> list = new ArrayList<ColumnConstraints>();
        for (int i = 0; i < root.getColumnCount(); i++) {
            list.add(new ColumnConstraints(WINDOWWIDTH / root.getColumnCount()));
        }
        return list;
    }

    //Method adds row constraints for however many rows there are.
    private ArrayList<RowConstraints> createRowConstraints() {
        ArrayList<RowConstraints> list = new ArrayList<RowConstraints>();
        for (int i = 0; i < root.getRowCount(); i++) {
            list.add(new RowConstraints(WINDOWHEIGHT / root.getRowCount()));
        }
        return list;
    }

    //Method creates the view box for the active ticker.
    private void createActiveTickerViewBox() {
        //Create and style the background.
        StackPane activeTickerView = new StackPane();
        activeTickerView.setStyle("-fx-background-color: #" + primary + ";");    //Set background of the graph. valueOf converts a color hex code to a JavaFX Paint object.
        activeTickerView.setPrefSize(WINDOWWIDTH, 300);

        //THE BELOW BLOCK OF CODE IS TEMPORARY
        Rectangle tempBox = new Rectangle();
        tempBox.setFill(Paint.valueOf("4E4E4E"));
        tempBox.setHeight(300);
        tempBox.setWidth(Math.floor(WINDOWWIDTH * .95));
        Label tempLabel = new Label();
        tempLabel.setText("Active ticker data view goes here.");
        tempLabel.setStyle("-fx-background-color: #" + accent + ";");
        activeTickerView.getChildren().addAll(tempBox, tempLabel);
        activeTickerView.setAlignment(tempBox, Pos.CENTER);


        root.add(activeTickerView, 0, 1, 5, 5);
        activeTickerView.getChildren().add(createTableView());
        //activeTickerView.setPadding(new Insets(0, WINDOWWIDTH - Math.floor(WINDOWWIDTH * 0.95), 0,WINDOWWIDTH - Math.floor(WINDOWWIDTH * 0.95)));

    }

    //Method creates the CandleStick Graph
    private void createCandleStickGraph() {
        //Create and style the background.
        StackPane candlestickGraph = new StackPane();
        candlestickGraph.setStyle("-fx-background-color: #" + primary + ";");    //Set background of the graph. valueOf converts a color hex code to a JavaFX Paint object.
        candlestickGraph.setPrefSize(WINDOWWIDTH, 240);

        //THE BELOW BLOCK OF CODE IS TEMPORARY
        Rectangle tempBox = new Rectangle();
        tempBox.setFill(Paint.valueOf("4E4E4E"));
        tempBox.setHeight(240);
        tempBox.setWidth(Math.floor(WINDOWWIDTH * .95));
        Label tempLabel = new Label();
        tempLabel.setText("Candlestick graph goes here.");
        tempLabel.setStyle("-fx-background-color: #" + accent + ";");
        candlestickGraph.getChildren().addAll(tempBox, tempLabel);
        candlestickGraph.setAlignment(tempBox, Pos.CENTER);


        root.add(candlestickGraph, 0, 7, 5, 4);
    }

    private void createFavoritesMenu(){
        //Create Menu Bar with 3 different menus
        MenuBar favoritesMenus = new MenuBar();
        Menu actions = new Menu("Actions");
        Menu stocks = new Menu("Stocks");
        Menu cryptos = new Menu("Cryptos");
        favoritesMenus.getMenus().addAll(actions, stocks, cryptos);
        //Create buttons for the actions menu
        MenuItem addButton = new MenuItem("Add");
        addButton = UI.addActionFunction(addButton, createChoiceDialog("Add"), favoritesMenus);
        addButton.setStyle("-fx-font-weight: bold");
        MenuItem removeButton = new MenuItem("Remove");
        removeButton = UI.removeActionFunction(removeButton, createChoiceDialog("Remove"), favoritesMenus);
        removeButton.setStyle("-fx-font-weight: bold");
        actions.getItems().addAll(addButton, removeButton);

        //Add to HBox layout to make layout look nicer
        HBox middleOptionsLayout = new HBox(10);
        middleOptionsLayout.getChildren().add(favoritesMenus);
        middleOptionsLayout.setTranslateX(32);
        middleOptionsLayout.setAlignment(Pos.CENTER_LEFT);

        //Add the buttons for the middle bar to the Grid Pane
        root.add(middleOptionsLayout, 0, 6, 2, 1);
    }

    //Creates the dialog window that lets the user choose stock or crypto
    private TextInputDialog createChoiceDialog(String text){
        TextInputDialog choiceWindow = new TextInputDialog();   //Create dialog window
        GridPane dialogContent = new GridPane();   //Create layout for dialog window
        choiceWindow.getDialogPane().getButtonTypes().remove(0);    //Remove the "OK" default button

        //Create the drop down menu for selecting which type of ticker you have
        ComboBox<String> typeSelection = new ComboBox<String>();
        typeSelection.getItems().addAll("Stocks", "Cryptos");
        typeSelection = styleDropDownMenu(typeSelection);
        dialogContent.add(typeSelection, 0, 0);
        

        //Create text field that takes user input
        TextField inputArea = new TextField();
        inputArea.setPromptText("Enter ticker: ");
        dialogContent.add(inputArea, 1, 0);

        //This will create either add buttons or remove buttons depending on which is passed into the method
        if (text == "Add") {
            //Create add button and add its functionality
            dialogContent.add(new Button(text), 2, 0);
        } else {
            //Create remove button and add its functionality
            dialogContent.add(new Button(text), 2, 0);
        }

        choiceWindow.getDialogPane().setContent(dialogContent);
        choiceWindow = styleChoiceDialog(choiceWindow);
        return choiceWindow;
    }

    //Style the type drop down menu in the dialog window
    private ComboBox<String> styleDropDownMenu(ComboBox<String> cb) {
        //CellFactory that styles each item in the drop down menu
        cb.setCellFactory(
            new Callback<ListView<String>, ListCell<String>>() {
                @Override public ListCell<String> call(ListView<String> param) {
                    final ListCell<String> cell = new ListCell<String>() {   
                        @Override public void updateItem(String item, 
                            boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    //Style the cell
                                    setText(item);    
                                    setTextFill(Paint.valueOf(white));
                                    setBackground(new Background(new BackgroundFill(Paint.valueOf(secondary), null, null)));
                                }
                                else {
                                    
                                }
                            }
                };
                return cell;
            }
        });

        return cb;
    }

    //Meethod styles the dialog window
    private TextInputDialog styleChoiceDialog(TextInputDialog td) {
        td.getDialogPane().getContent().setStyle("-fx-background-color: #" + primary + ";");
        td.getDialogPane().setStyle("-fx-background-color: #" + primary + ";");
        td.setHeaderText(null);
        td.setGraphic(null);

        GridPane gp = (GridPane)td.getDialogPane().getContent();
        //Style the text field
        gp.getChildren().get(1).setStyle("-fx-background-color: #" + secondary + ";" 
        + "-fx-text-fill: #" + white + ";");
        //Style the type drop down menu
        gp.getChildren().get(0).setStyle("-fx-background-color: #" + secondary + ";");
        //Style the remove/add button
        gp.getChildren().get(2).setStyle("-fx-background-color: #" + secondary + ";" 
        + "-fx-text-fill: #" + white + ";");
        //Add padding between buttons
        gp.setHgap(10);
        
        td.getDialogPane().setContent(gp);
        return td;
    }

    //Method creates the buttons at the top of the window
    private void createTopBar() {
        //Button that saves the active ticker data
        Button saveBtn = new Button();
        saveBtn.setText("Save");
        //Button that loads the active ticker data
        Button loadTickerDataBtn = new Button();
        loadTickerDataBtn.setText("Load");
        //Groups the two above buttons together
        HBox leftSide = new HBox(10);
        leftSide.getChildren().addAll(saveBtn, loadTickerDataBtn);
        leftSide.setTranslateX(32);
        leftSide.setAlignment(Pos.CENTER_LEFT);

        //Button that updates the active ticker data
        Button updateTickerDataBtn = new Button();
        updateTickerDataBtn.setText("Update");
        //Button that updates all ticker data
        Button updateAllTickersBtn = new Button();
        updateAllTickersBtn.setText("Update All");
        //Groups the two above buttons together
        HBox rightSide = new HBox(10);
        rightSide.getChildren().addAll(updateTickerDataBtn, updateAllTickersBtn);
        rightSide.setTranslateX(-32);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        //Add the buttons to the top bar section of the GridPane
        root.add(leftSide, 0, 0, 2, 1);
        root.add(rightSide, 3, 0, 2, 1);
    }

    //Method creates the bottom section of buttons. Only includes the Max Profit Button right now.
    private void createBottomBar() {
        //Button that runs the max profit algorithm
        Button maxProfitBtn = new Button();
        maxProfitBtn.setText("MAX PROFIT!!!!!");
        root.setHalignment(maxProfitBtn, HPos.CENTER);

        //Add bottom buttons to the GridPane
        root.add(maxProfitBtn, 2, 11, 1, 1);
    }

    //Method creates a TableView for viewing Entries

    /**
     * Creates and returns the TableView control
     *
     * @return TableView control
     */
    private TableView createTableView() {
        TableView entryTable = new TableView();

        // set up columns
        TableColumn<Entry, String> openColumn = new TableColumn<>("Open");
        openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));

        TableColumn<Entry, String> closeColumn = new TableColumn<>("Close");
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("close"));

        TableColumn<Entry, String> lowColumn = new TableColumn<>("Low");
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));

        TableColumn<Entry, String> highColumn = new TableColumn<>("High");
        highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));

        TableColumn<Entry, String> volumeColumn = new TableColumn<>("Volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

        TableColumn<Entry, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // add all the columns to the table
        entryTable.getColumns().setAll(openColumn, closeColumn, lowColumn, highColumn, volumeColumn, dateColumn);

        // set the size of the table to fit the temporary gray rectangle
        entryTable.setMaxSize(Math.floor(WINDOWWIDTH * .95), 300);
        entryTable.setPrefSize(Math.floor(WINDOWWIDTH * .95), 300);

        return entryTable;

    }
}