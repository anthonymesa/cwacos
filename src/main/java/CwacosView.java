/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

import java.lang.Math;

public class CwacosView extends Application {

    //Window dimensions
    private final int WINDOWWIDTH = 1280;
    private final int WINDOWHEIGHT = 720;

    CwacosUI UI = new CwacosUI();

    //Parent layout
    private GridPane root = new GridPane();


    //Active ticket selection drop down menu
    private MenuBar middleOptions = new MenuBar();

    public static void beginUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cwacos");

        //Call methods to add buttons to gridpane
        createTopBar();
        createActiveTickerViewBox();
        createMiddleBar();
        createCandleStickGraph();
        createBottomBar();

        setupGridPane();
        primaryStage.setScene(new Scene(root, WINDOWWIDTH, WINDOWHEIGHT));
        primaryStage.show();
    }

    //Fucntion sets styling of the gridpane and performs other necessary operations.
    public void setupGridPane() {
        root.setStyle("-fx-background-color: #1D1D1D;");   //Background color
        //Set grid spacing
        root.getColumnConstraints().addAll(createColumnConstraints());
        root.getRowConstraints().addAll(createRowConstraints());
    }

    //Method adds column constraints for however many columns there are.
    public ArrayList<ColumnConstraints> createColumnConstraints() {
        ArrayList<ColumnConstraints> list = new ArrayList<ColumnConstraints>();
        for (int i = 0; i < root.getColumnCount(); i++) {
            list.add(new ColumnConstraints(WINDOWWIDTH / root.getColumnCount()));
        }
        return list;
    }

    //Method adds row constraints for however many rows there are.
    public ArrayList<RowConstraints> createRowConstraints() {
        ArrayList<RowConstraints> list = new ArrayList<RowConstraints>();
        for (int i = 0; i < root.getRowCount(); i++) {
            list.add(new RowConstraints(WINDOWHEIGHT / root.getRowCount()));
        }
        return list;
    }

    //Method creates the view box for the active ticker.
    public void createActiveTickerViewBox() {
        //Create and style the background.
        StackPane activeTickerView = new StackPane();
        activeTickerView.setStyle("-fx-background-color: #1D1D1D;");    //Set background of the graph. valueOf converts a color hex code to a JavaFX Paint object.
        activeTickerView.setPrefSize(WINDOWWIDTH, 300);

        //THE BELOW BLOCK OF CODE IS TEMPORARY
        Rectangle tempBox = new Rectangle();
        tempBox.setFill(Paint.valueOf("4E4E4E"));
        tempBox.setHeight(300);
        tempBox.setWidth(Math.floor(WINDOWWIDTH * .95));
        Label tempLabel = new Label();
        tempLabel.setText("Active ticker data view goes here.");
        tempLabel.setStyle("-fx-background-color: #BB86FC;");
        activeTickerView.getChildren().addAll(tempBox, tempLabel);
        activeTickerView.setAlignment(tempBox, Pos.CENTER);


        root.add(activeTickerView, 0, 1, 5, 5);
    }

    //Method creates the CandleStick Graph
    public void createCandleStickGraph() {
        //Create and style the background.
        StackPane candlestickGraph = new StackPane();
        candlestickGraph.setStyle("-fx-background-color: #1D1D1D;");    //Set background of the graph. valueOf converts a color hex code to a JavaFX Paint object.
        candlestickGraph.setPrefSize(WINDOWWIDTH, 240);

        //THE BELOW BLOCK OF CODE IS TEMPORARY
        Rectangle tempBox = new Rectangle();
        tempBox.setFill(Paint.valueOf("4E4E4E"));
        tempBox.setHeight(240);
        tempBox.setWidth(Math.floor(WINDOWWIDTH * .95));
        Label tempLabel = new Label();
        tempLabel.setText("Candlestick graph goes here.");
        tempLabel.setStyle("-fx-background-color: #BB86FC;");
        candlestickGraph.getChildren().addAll(tempBox, tempLabel);
        candlestickGraph.setAlignment(tempBox, Pos.CENTER);


        root.add(candlestickGraph, 0, 7, 5, 4);
    }

    //Method creates the buttons at the top of the window
    public void createTopBar() {
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

    //Creates the buttons that are betweent he active ticker view and the candlestick graph view
    public void createMiddleBar() {
        //Creates the parent menu and adds the submenus
        middleOptions.getMenus().add(new Menu("Favorites"));
        middleOptions.getMenus().get(0).getItems().addAll(new Menu("Stocks"), new Menu("Cryptos"));

        //Text input area to add a new ticker
        TextInputDialog addTickerDialog = new TextInputDialog("Ticker goes here");
        addTickerDialog.setHeaderText("Add Ticker");
        Button addTicker = new Button();
        addTicker.setText("Add");
        //Add functionality to button
        //addTicker = UI.addTickerFunction(addTickerDialog, middleOptions.getMenus().get(0), addTicker);

        //Button to remove a ticker from the list
        TextInputDialog removeTickerDialog = new TextInputDialog("Ticker goes here");
        removeTickerDialog.setHeaderText("Remove Ticker");
        Button removeTicker = new Button();
        removeTicker.setText("Remove");
        //Add functionality to button
        //removeTicker = UI.removeTickerFunction(removeTickerDialog, favorites, removeTicker);

        HBox middleOptionsLayout = new HBox(10);
        middleOptionsLayout.getChildren().addAll(middleOptions, addTicker, removeTicker);
        middleOptionsLayout.setTranslateX(32);
        middleOptionsLayout.setAlignment(Pos.CENTER_LEFT);

        //Add the buttons for the middle bar to the Grid Pane
        root.add(middleOptionsLayout, 0, 6, 2, 1);
    }

    //Method creates the bottom section of buttons. Only includes the Max Profit Button right now.
    public void createBottomBar() {
        //Button that runs the max profit algorithm
        Button maxProfitBtn = new Button();
        maxProfitBtn.setText("MAX PROFIT!!!!!");
        root.setHalignment(maxProfitBtn, HPos.CENTER);

        //Add bottom buttons to the GridPane
        root.add(maxProfitBtn, 2, 11, 1, 1);
    }
}