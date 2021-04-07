import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public class CwacosView extends Application {

    //Window dimensions
    private final int WINDOWWIDTH = 1280;
    private final int WINDOWHEIGHT = 720;

    CwacosUI UI = new CwacosUI();

    //Parent layout
    public GridPane root = new GridPane();


    //Active ticket selection drop down menu
    public ComboBox<String> activeTickerMenu = new ComboBox<String>();

    public static void beginUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cwacos");
        
        //Call methods to add buttons to gridpane
        createTopBar();
        createMiddleBar();
        createCandleStickGraph();
        createBottomBar();

        setupGridPane();
        primaryStage.setScene(new Scene(root, WINDOWWIDTH, WINDOWHEIGHT));
        primaryStage.show();
    }

    //Fucntion sets styling of the gridpane and performs other necessary operations.
    public void setupGridPane(){
        root.setStyle("-fx-background-color: #BFFFFE;");   //Background color
        //Set grid spacing
        root.getColumnConstraints().addAll(createColumnConstraints());
        root.getRowConstraints().addAll(createRowConstraints());
    }

    //Method adds column constraints for however many columns there are.
    public ArrayList<ColumnConstraints> createColumnConstraints() {
        ArrayList<ColumnConstraints> list = new ArrayList<ColumnConstraints>();
        for (int i = 0; i < root.getColumnCount(); i++) {
            list.add(new ColumnConstraints(WINDOWWIDTH/root.getColumnCount()));
        }
        return list;
    }

    //Method adds row constraints for however many rows there are.
    public ArrayList<RowConstraints> createRowConstraints(){
        ArrayList<RowConstraints> list = new ArrayList<RowConstraints>();
        for (int i = 0; i < root.getRowCount(); i++) {
            list.add(new RowConstraints(WINDOWHEIGHT/root.getRowCount()));
        }
        return list;
    }

    //Method creates the CandleStick Graph
    public void createCandleStickGraph(){
        //Create and style the background.
        Rectangle background = new Rectangle();
        background.setFill(Paint.valueOf("B9B9B9"));    //Set background of the graph
        background.setHeight(20);
        background.setWidth(1280);
        root.add(background, 0, 7, 5, 4);
    }

    public void createTopBar() {
        //Button that saves the active ticker data
        Button saveBtn = new Button();
        saveBtn.setText("Save");

        //Button that loads the active ticker data
        Button loadTickerDataBtn = new Button();
        loadTickerDataBtn.setText("Load");

        //Button that updates the active ticket data
        Button updateTickerDataBtn = new Button();
        updateTickerDataBtn.setText("Update");
        
        //Button that updates all ticker data
        Button updateAllTickersBtn = new Button();
        updateAllTickersBtn.setText("Update All");

        //Add the buttons to the top bar section of the GridPane
        root.add(saveBtn, 0, 0, 1, 1);
        root.add(loadTickerDataBtn, 1, 0, 1, 1);
        root.add(updateTickerDataBtn, 3, 0, 1, 1);
        root.add(updateAllTickersBtn, 4, 0, 1, 1);
    }

    public void createMiddleBar() {
        activeTickerMenu.setPromptText("Favorites");
        root.setHalignment(activeTickerMenu, HPos.CENTER);

        //Text input area to add a new ticker
        TextInputDialog addTickerDialog = new TextInputDialog("Ticker goes here");
        addTickerDialog.setHeaderText("Add Ticker");
        Button addTicker = new Button();
        addTicker.setText("Add");
        UI.addDialogFunction(addTickerDialog, activeTickerMenu, addTicker);

        //Button to remove a ticker from the list
        TextInputDialog removeTickerDialog = new TextInputDialog("Ticker goes here");
        removeTickerDialog.setHeaderText("Remove Ticker");
        Button removeTicker = new Button();
        removeTicker.setText("Remove");

        //Add the buttons for the middle bar to the Grid Pane
        root.add(activeTickerMenu, 0, 6, 1, 1);
        root.add(addTicker, 1, 6, 1, 1);
        root.add(removeTicker, 2, 6, 1, 1);
    }

    public void createBottomBar(){
        //Button that runs the max profit algorithm
        Button maxProfitBtn = new Button();
        maxProfitBtn.setText("MAX PROFIT!!!!!");
        maxProfitBtn.setAlignment(Pos.CENTER);
        
        //Add bottom buttons to the GridPane
        root.add(maxProfitBtn, 2, 11, 1, 1);
    }
}