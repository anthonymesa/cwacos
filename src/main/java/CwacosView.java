import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.geometry.*;

public class CwacosView extends Application {

    CwacosUI UI = new CwacosUI();

    //Parent layout
    public VBox root = new VBox();

    public static void beginUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ArrayList<Button> topButtons = new ArrayList<Button>();

        primaryStage.setTitle("Hello World!");

        //This section is for the top HBox
        //Button that saves the active ticker data
        Button saveBtn = new Button();
        saveBtn.setText("Save");
        topButtons.add(saveBtn);
        //saveBtn.setGraphic(new ImageView(
        //new Image(getClass().getResourceAsStream("C:/Users/jackf/JavaProjects/cwacos/Cwacos/res/icons/saveicon.jpeg"))));

        //Button that loads the active ticker data
        Button loadTickerDataBtn = new Button();
        loadTickerDataBtn.setText("Load");
        topButtons.add(loadTickerDataBtn);

        //Button that updates the active ticket data
        Button updateTickerDataBtn = new Button();
        updateTickerDataBtn.setText("Update");
        topButtons.add(updateTickerDataBtn);
        
        //Button that updates all ticker data
        Button updateAllTickersBtn = new Button();
        updateAllTickersBtn.setText("Update All");
        topButtons.add(updateAllTickersBtn);

        //Top bar of buttons
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 12, 15, 12));
        topBar.setSpacing(10);
        topBar.getChildren().addAll(topButtons);


        //This section is for the bottom HBox
        //Button that runs the max profit algorithm
        Button maxProfitBtn = new Button();
        maxProfitBtn.setText("MAX PROFIT!!!!!");
        
        //HBox for max profit button
        HBox maxProfitHBox = new HBox();
        maxProfitHBox.getChildren().add(maxProfitBtn);
        maxProfitHBox.setAlignment(Pos.BOTTOM_CENTER);

        //This section is for the Chart/Table section

        //This section is for the middle HBox
        //Active ticket selection drop down menu
        ComboBox<String> activeTickerMenu = new ComboBox<String>();
        
        //Text input area to add a new ticker
        TextInputDialog addTickerDialog = new TextInputDialog("Ticker goes here");
        addTickerDialog.setHeaderText("Add Ticker");
        Button addTicker = new Button();
        addTicker.setText("Add");
        UI.addDialogFunction(addTickerDialog, addTicker);

        //Button to remove a ticker from the list
        TextInputDialog removeTickerDialog = new TextInputDialog("Ticker goes here");
        removeTickerDialog.setHeaderText("Remove Ticker");
        Button removeTicker = new Button();
        removeTicker.setText("Remove");
        UI.addDialogFunction(removeTickerDialog, removeTicker);

        //HBox that holds all of the middle 
        HBox middleBar = new HBox();
        middleBar.getChildren().addAll(activeTickerMenu, addTicker, removeTicker);

        //Add HBoxes to parent layout
        root.getChildren().add(topBar);
        root.getChildren().add(middleBar);
        root.getChildren().add(maxProfitHBox);
        
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }
}