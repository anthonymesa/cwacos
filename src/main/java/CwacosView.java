/*
Last updated:
Purpose of this class:
Contributing Authors: Jack Fink, Anthony Mesa, Hyoungjin Choi
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public class CwacosView extends Application {
    //enumerated label texts
    private final String CWACOS = "Cwacos";
    private final String LOAD = "Load";
    private final String CANCEL = "Cancel";
    private final String ADD = "Add";
    private final String REMOVE = "Remove";
    private final String UPDATE = "Update";
    private final String ADD_STOCK_OR_CRYPTO = "Add Stock/Crypto";
    private final String REMOVE_STOCK_OR_CRYPTO = "Remove Stock/Crypto";

    //Color palette
    private final String PRIMARY_COLOR = "1D1D1D";
    private final String SECONDARY_COLOR = "4E4E4E";
    private final String WHITE_COLOR = "FFFFFF";
    private final String ACCENT_COLOR = "BB86FC";

    //Status leading string
    private final String STATUS_LEAD = "  ~ ";

    // this row index is updated every time a row is added with the current row level
    // that the next row should be generated on;
    private int y_row_index = 0;

    //Window dimensions
    private final int WINDOWWIDTH = 1465;
    private final int WINDOWHEIGHT = 1000;

    //grid dimensions
    private final int GRID_X = 60;
    private final int GRID_Y = 60;

    //custom spacing
    private final int BUTTON_ICON_SIZE = 20;
    private final int BUTTON_SPACING = 2;
    private final int COMPONENT_SPACING = 10;

    // This label is used for outputting state messages to user. Defined at
    // class level because it will be referenced everywhere.
    private final Label STATUS_OUTPUT = new Label();
    private final MenuBar favoritesMenu = new MenuBar();
    private final TextInputDialog choiceWindow = new TextInputDialog();

    //table view for active data
    private TableView entryTable = new TableView();

    CwacosController UI = new CwacosController();

    //Parent layout
    private GridPane root = new GridPane();

    public static void beginUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // this is called from the UI so we can populate menu after settings have been loaded
        CwacosData.loadState();

        CwacosPopup.init();

        VBox content = new VBox();

        //Create text field that takes user input
        Label qfact = new Label(CwacosData.getQuakkaFact());
        content.getChildren().add(qfact);

        // call popup with above parameters
        CwacosPopup.display(CWACOS, "Woah!", content);

        primaryStage.setTitle(CWACOS);

        // Changing the order of these functions will change the order these rows
        // are displayed
        generateMenuBar(3, 1, 0);
        generateActiveDataTableComponent(32, 1, 0);
        generateCandlestickGraphComponent(24, 1, 1);
        generateStatusBar(1, 1, 0);

        setupGridPane();
        primaryStage.setScene(new Scene(root, WINDOWWIDTH, WINDOWHEIGHT));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        CwacosData.saveState();
    }

    //============================== GRID SETUP =================================

    /**
     *
     */
    private void setupGridPane() {
        root.setStyle("-fx-background-color: #" + PRIMARY_COLOR + ";");   //Background color

        //Set grid spacing
        //root.getColumnConstraints().addAll(createColumnConstraints());
        //root.getRowConstraints().addAll(createRowConstraints());

        for (int i = 0; i < GRID_X; i++) {
            root.getColumnConstraints().add(new ColumnConstraints(WINDOWWIDTH / GRID_X));
        }

        for (int i = 0; i < GRID_Y; i++) {
            root.getRowConstraints().add(new RowConstraints(WINDOWHEIGHT / GRID_Y));
        }
    }

    //============================ MENU BAR =================================

    /**
     * Generates top bar of UI
     * <p>
     * Gets the MenuBarComponent and adds it to the root grid pane
     *
     * @param height
     * @param w_margin
     * @param h_margin
     */
    private void generateMenuBar(int height, int w_margin, int h_margin) {

        //Add the button container to the top bar section of the GridPane
        root.add(generateMenuBarComponentLeft(height, w_margin, h_margin), w_margin, y_row_index, (GRID_X / 2) - w_margin, height);

        // There is an odd thing going on here, technically the width should be GRID_X / 2 - (w_margin / 2) but it isn't and I don't know why.
        root.add(generateMenuBarComponentRight(height, w_margin, h_margin), (GRID_X / 2), y_row_index, (GRID_X / 2), height);

        // update the y_index for the next row
        y_row_index += height;
    }

    //=========================== LEFT MENU BAR =========================

    /**
     * @param height
     * @param w_margin
     * @param h_margin
     * @return
     */
    private HBox generateMenuBarComponentLeft(int height, int w_margin, int h_margin) {
        HBox menuBar = new HBox();

        //menuBar.setStyle("-fx-background-color: #"+ WHITE_COLOR + ";");
        menuBar.setSpacing(COMPONENT_SPACING);

        // Add buttons to menu bar hbox
        menuBar.getChildren().addAll(
                generateSaveLoadOperationsComponent(),
                generateFavoritesOperationsComponent()
        );

        return menuBar;
    }

    /**
     * Creates HBox section containing buttons for
     * saving and loading operations.
     *
     * @return
     */
    private HBox generateSaveLoadOperationsComponent() {

        HBox buttonContainer = new HBox();

        buttonContainer.setAlignment(Pos.CENTER_LEFT);
        buttonContainer.setSpacing(BUTTON_SPACING);

        // Add buttons to "Save Load" hbox
        buttonContainer.getChildren().addAll(
                generateSaveActiveDataBtn(),
                generateLoadFileToActiveDataBtn()
        );

        return buttonContainer;
    }

    /**
     * Generate button for saving data in active view
     *
     * @return
     */
    public Button generateSaveActiveDataBtn() {

        Button saveBtn = new Button();

        Image img = new Image("file:res/save.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        saveBtn.setGraphic(view);

        saveBtn.setStyle("-fx-border-width: 0px; ");
        saveBtn.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        saveBtn.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        saveBtn.setTooltip(new Tooltip("Save data to file"));

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // Save data related to given symbol to file at given url
                String save_status = CwacosData.saveData();

                // Set label to the output status that CwacosData gives back.
                setStatus(save_status);
            }
        });

        return saveBtn;
    }

    /**
     * Generate button for loading data into active view
     *
     * @return
     */
    public Button generateLoadFileToActiveDataBtn() {

        Button loadBtn = new Button();

        Image img = new Image("file:res/folder.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        loadBtn.setGraphic(view);

        loadBtn.setStyle("-fx-border-width: 0px; ");
        loadBtn.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        loadBtn.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        loadBtn.setTooltip(new Tooltip("Load data from file"));

        loadBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                HBox content = new HBox();

                //Create text field that takes user input
                TextField inputArea = new TextField();
                inputArea.setPromptText("Enter file url: ");
                content.getChildren().add(inputArea);

                // define function to be run when user clicks 'okay'
                Function<Object, Object> testFunction = new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {

                        //Store the string value of the ticker the user inputted.
                        String file_url = inputArea.getText();

                        ArrayList<String> load_parameters = new ArrayList<String>(
                                Arrays.asList(
                                        file_url
                                )
                        );

                        //ATTN: need to handle when loadData doesnt work
                        String loadDataStatus = CwacosData.loadData(load_parameters);

                        setStatus(loadDataStatus);

                        // update table view to be populated with the data from the data loaded in the map.
                        populateTable(entryTable);

                        return 0;
                    }
                };

                // call popup with above parameters
                CwacosPopup.display(LOAD, LOAD, CANCEL, content, testFunction);

            }
        });

        return loadBtn;
    }


    private HBox generateFavoritesOperationsComponent() {
        HBox favoritesContainer = new HBox();

        favoritesContainer.setAlignment(Pos.CENTER_LEFT);
        favoritesContainer.setSpacing(BUTTON_SPACING);

        // Add buttons to "Update UpdateAll" hbox
        favoritesContainer.getChildren().addAll(
                generateAddFavoriteBtn(),
                generateRemoveFavoriteBtn(),
                generateStocksCryptoMenu()
        );

        return favoritesContainer;
    }

    private Button generateAddFavoriteBtn() {
        Button addBtn = new Button();

        Image img = new Image("file:res/plus-square.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        addBtn.setGraphic(view);

        addBtn.setStyle("-fx-border-width: 0px; ");
        addBtn.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        addBtn.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        addBtn.setTooltip(new Tooltip("Add favorite"));

        // Set action for clicking 'add favorite'
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                HBox content = new HBox();

                //Create the drop down menu for selecting which type of ticker you have
                ComboBox<String> typeSelection = createComboBox();
                typeSelection.getItems().addAll("Stocks", "Cryptos");
                //typeSelection = styleDropDownMenu(typeSelection);
                //styleDropDownMenu(typeSelection);
                content.getChildren().add(typeSelection);

                //Create text field that takes user input
                TextField inputArea = new TextField();
                inputArea.setPromptText("Enter ticker: ");
                content.getChildren().add(inputArea);

                // define function to be run when user clicks 'okay'
                Function<Object, Object> testFunction = new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {

                        //Store the string value of the ticker the user inputted.
                        String symbol = inputArea.getText();

                        // get int type based on position in list.
                        int type = typeSelection.getSelectionModel().getSelectedIndex();

                        // Checks that a type is actually selected from the dropdown
                        if (type < 0) {
                            return -1;
                        } else {
                            String addFavoriteStatus = CwacosData.addFavorite(symbol, type);

                            // check to see if the symbol was added to the data, if not, exit
                            if (!CwacosData.existData(symbol)) {
                                setStatus(addFavoriteStatus);
                                return -1;
                            }

                            //ATTN: this is dependent on the case that there are always the same number
                            // of items in the combo box as in the menubar types (stocks, cryptos)
                            favoritesMenu.getMenus().get(type).getItems().add(new MenuItem(symbol.toUpperCase()));

                            setStatus(addFavoriteStatus);
                            CwacosData.setActiveData(symbol);

                            return 0;
                            // update table view to be populated with the data from the data in the map.
                            // for all intents and purposes this should create an empty table.
                        }
                    }
                };

                // call popup with above parameters
                CwacosPopup.display(ADD_STOCK_OR_CRYPTO, ADD, CANCEL, content, testFunction);
            }
        });

        return addBtn;
    }

    private Button generateRemoveFavoriteBtn() {
        Button saveBtn = new Button();

        Image img = new Image("file:res/minus-square.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        saveBtn.setGraphic(view);

        saveBtn.setStyle("-fx-border-width: 0px; ");
        saveBtn.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        saveBtn.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        saveBtn.setTooltip(new Tooltip("Remove favorite"));

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                HBox content = new HBox();

                //Create the drop down menu for selecting which type of ticker you have
                ComboBox<String> typeSelection = createComboBox();
                typeSelection.getItems().addAll("Stocks", "Cryptos");
                //typeSelection = styleDropDownMenu(typeSelection);
                //styleDropDownMenu(typeSelection);
                content.getChildren().add(typeSelection);

                //Create text field that takes user input
                TextField inputArea = new TextField();
                inputArea.setPromptText("Enter ticker: ");
                content.getChildren().add(inputArea);

                Function<Object, Object> testFunction = new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {

                        // Get string from user input
                        String symbol = inputArea.getText().toUpperCase();

                        //Get the type the user selected
                        int type = typeSelection.getSelectionModel().getSelectedIndex();

                        // if type is -1, nothing in the combobox is selected
                        if (type < 0) {
                            return -1;
                        } else {
                            String removeFavoriteStatus = CwacosData.removeFavorites(symbol);

                            // check to see if the symbol was properly removed
                            if (CwacosData.existData(symbol)) {
                                setStatus(removeFavoriteStatus);
                                return -1;
                            }

                            //Pick one of the ticker menus depending on ticker type selection
                            Menu menu = favoritesMenu.getMenus().get(type);

                            //Search the menu for the ticker to remove
                            for (int i = 0; i < menu.getItems().size(); i++) {
                                MenuItem current = menu.getItems().get(i);  //Store the current menu item that's being checked

                                String text = current.getText();    //Store the string contained in the menu item

                                //Check if the ticker the user entered matches the current menu item
                                if (text.contains(symbol)) {
                                    menu.getItems().remove(current);    //Remove that ticker from the menu
                                }
                            }

                            setStatus(removeFavoriteStatus);

                            // need to set active table view data either to empty or another symbol in the list
                            // CwacosData.setActiveData(symbol);

                            return 0;
                        }
                    }
                };

                CwacosPopup.display(REMOVE_STOCK_OR_CRYPTO, REMOVE, CANCEL, content, testFunction);
            }
        });

        return saveBtn;
    }

    private HBox generateStocksCryptoMenu() {

        Menu stocks = new Menu("Stocks");
        Menu cryptos = new Menu("Cryptos");

        favoritesMenu.getMenus().addAll(stocks, cryptos);

        //Add to HBox layout to make layout look nicer
        HBox middleOptionsLayout = new HBox();
        middleOptionsLayout.getChildren().add(favoritesMenu);
        middleOptionsLayout.setAlignment(Pos.CENTER_LEFT);

        return middleOptionsLayout;
    }

    //=========================== RIGHT MENU BAR =========================

    /**
     * @param height
     * @param w_margin
     * @param h_margin
     * @return
     */
    private HBox generateMenuBarComponentRight(int height, int w_margin, int h_margin) {
        HBox menuBar = new HBox();

        //menuBar.setStyle("-fx-background-color: #"+ WHITE_COLOR + ";");
        menuBar.setAlignment(Pos.CENTER_RIGHT);
        menuBar.setSpacing(COMPONENT_SPACING);

        // Add buttons to menu bar hbox
        menuBar.getChildren().addAll(
                generateMaxProfitComponent(),
                generateUpdateOperationsComponent()
        );

        return menuBar;
    }

    private HBox generateMaxProfitComponent() {

        HBox buttonContainer = new HBox();

        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setSpacing(BUTTON_SPACING);

        // Add buttons to "Update UpdateAll" hbox
        buttonContainer.getChildren().addAll(
                generateMaxProfitButton()
        );

        return buttonContainer;
    }

    private Button generateMaxProfitButton() {

        Button maxProfitBtn = new Button();

        Image img = new Image("file:res/dollar-sign.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        maxProfitBtn.setGraphic(view);

        maxProfitBtn.setStyle("-fx-border-width: 0px; ");
        maxProfitBtn.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        maxProfitBtn.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        maxProfitBtn.setTooltip(new Tooltip("Run MaxProfit algorithm"));

        // Set action for clicking 'add favorite'
        maxProfitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                VBox content = new VBox();

                //Create text field that takes user input
                Label low = new Label("Lowest was: ");
                Label buy = new Label("Buy price was: ");
                Label high = new Label("Highest was : ");
                Label sell = new Label("Sell price was: ");
                Label profit = new Label("Profit was: ");
                content.getChildren().addAll(low, buy, high, sell, profit);

                // call popup with above parameters
                CwacosPopup.display("Max Analysis", "Nice", content);
            }
        });

        return maxProfitBtn;
    }

    /**
     * Creates HBox section for containing data update operations
     *
     * @return
     */
    private HBox generateUpdateOperationsComponent() {

        HBox buttonContainer = new HBox();

        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setSpacing(BUTTON_SPACING);

        // Add buttons to "Update UpdateAll" hbox
        buttonContainer.getChildren().addAll(
                generateUpdateActiveDataBtn(),
                generateUpdateAllDataBtn()
        );

        return buttonContainer;
    }

    /**
     * Generate button for updating data in active view
     *
     * @return
     */
    public Button generateUpdateActiveDataBtn() {

        Button updateBtn = new Button();

        Image img = new Image("file:res/rotate-cw.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        updateBtn.setGraphic(view);

        updateBtn.setStyle("-fx-border-width: 0px; ");
        updateBtn.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        updateBtn.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        updateBtn.setTooltip(new Tooltip("Update data for table in view"));

        updateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                HBox content = new HBox();
                GridPane dialogContent = new GridPane();

                // combo box for call type
                // this could be done programmatically, technically, but we may not always use enums given the API we use,
                // so this will be done custom
                final ComboBox<String> callTypeList = createComboBox();
                callTypeList.getItems().addAll("Intraday", "Daily", "Weekly", "Monthly");
                dialogContent.add(callTypeList, 0, 0);

                // combo box for interval type
                final ComboBox<String> callIntervalList = createComboBox();
                callIntervalList.getItems().addAll("None", "1 Min.", "5 Min.", "10 Min.", "15 Min.", "30 Min.", "60 Min.");
                dialogContent.add(callIntervalList, 0, 1);

                // define function to be run when user clicks 'okay'
                Function<Object, Object> testFunction = new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {

                        int type = callTypeList.getSelectionModel().getSelectedIndex();
                        int interval = callIntervalList.getSelectionModel().getSelectedIndex();

                        // check that type and interval have been chosen
                        if ((type < 0) || (interval < 0)) {
                            return -1;
                        } else {
                            String updateStatus = CwacosData.update(type, interval);

                            // if status is not null, there was an error
                            if (updateStatus != null) {
                                setStatus(updateStatus);
                                return -1;
                            }

                            // set active table view to the activeData
                            populateTable(entryTable);
                            setStatus("Success: Updated " + CwacosData.getActiveData());

                            return 0;
                        }
                    }
                };

                content.getChildren().add(dialogContent);

                // call popup with above parameters
                CwacosPopup.display(UPDATE, UPDATE, CANCEL, content, testFunction);
            }
        });

        return updateBtn;
    }

    /**
     * Generate button for updataing all data in active view
     *
     * @return
     */
    public Button generateUpdateAllDataBtn() {

        Button updateAllBtn = new Button();

        Image img = new Image("file:res/refresh-cw.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        updateAllBtn.setGraphic(view);

        updateAllBtn.setStyle("-fx-border-width: 0px; ");
        updateAllBtn.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        updateAllBtn.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        updateAllBtn.setTooltip(new Tooltip("Update all data"));

        updateAllBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //String updateAllStatus = CwacosData.updateAll();
                //setStatus(updateAllStatus);

                setStatus("This code is currently commented out.");
            }
        });

        return updateAllBtn;
    }

    //================================= ACTIVE DATA TABLE ===============================

    //Method creates the view box for the active ticker.
    private void generateActiveDataTableComponent(int height, int w_margin, int h_margin) {

        StackPane activeTickerView = new StackPane();

        activeTickerView.setStyle("-fx-background-color: #" + PRIMARY_COLOR + ";");

        activeTickerView.getChildren().add(createTableView(height, w_margin, h_margin));

        root.add(activeTickerView, w_margin, y_row_index, GRID_X - w_margin, height);

        y_row_index += height;
    }

    /**
     * Creates and returns the TableView control
     *
     * @param _height
     * @param _wMargin
     * @param _hMargin
     * @return TableView control
     */
    private TableView createTableView(int _height, int _wMargin, int _hMargin) {

        // set up columns
        TableColumn<Entry, String> openColumn = new TableColumn<>("Open");
        openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));
        openColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Entry, String> closeColumn = new TableColumn<>("Close");
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("close"));
        closeColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Entry, String> lowColumn = new TableColumn<>("Low");
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));
        lowColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Entry, String> highColumn = new TableColumn<>("High");
        highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));
        highColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Entry, String> volumeColumn = new TableColumn<>("Volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        volumeColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Entry, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateColumn.setStyle("-fx-alignment: CENTER;");

        // add all the columns to the table
        entryTable.getColumns().setAll(openColumn, closeColumn, lowColumn, highColumn, volumeColumn, dateColumn);
        entryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return entryTable;
    }

    /**
     * Populates the table with entries from the API.
     */
    private void populateTable(TableView<Entry> _table) {
        // clear table entries if there are any
        if (!_table.getItems().isEmpty()) {
            _table.getItems().clear();
            _table.refresh();
        }

        // populate the table
        ArrayList<Entry> entries = CwacosData.getActiveEntryList();
        for (Entry entry : entries) {
            _table.getItems().add(entry);
        }
    }

    /**
     * Creates then returns a Combo Box.
     *
     * @return Combo Box with a prompt text.
     */
    private ComboBox<String> createComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Select...");
        return comboBox;
    }


    //================================= CANDLESTICK GRAPH ===============================

    //Method creates the CandleStick Graph
    private void generateCandlestickGraphComponent(int height, int w_margin, int h_margin) {
        //Create and style the background.
        StackPane candlestickGraph = new StackPane();
        candlestickGraph.setStyle("-fx-background-color: #" + PRIMARY_COLOR + ";");    //Set background of the graph. valueOf converts a color hex code to a JavaFX Paint object.
        //candlestickGraph.setStyle("-fx-background-color: #" + WHITE_COLOR + ";");

        //THE BELOW BLOCK OF CODE IS TEMPORARY
        Rectangle tempBox = new Rectangle();
        tempBox.setFill(Paint.valueOf("4E4E4E"));

        tempBox.setHeight((WINDOWHEIGHT / GRID_Y) * (height - (h_margin * 2)));
        tempBox.setWidth((WINDOWWIDTH / GRID_X) * (GRID_X - w_margin));

        Label candlestick = new Label();
        candlestick.setText("Candlestick graph goes here.");
        candlestick.setStyle("-fx-background-color: #" + ACCENT_COLOR + ";");

        candlestickGraph.getChildren().addAll(tempBox, candlestick);
        candlestickGraph.setAlignment(tempBox, Pos.CENTER);

        root.add(candlestickGraph, w_margin, y_row_index, GRID_X - w_margin, height);

        y_row_index += height;
    }

    //============================= STATUS BAR =================================

    /**
     * Generate bottom bar for status output
     */
    private void generateStatusBar(int height, int w_margin, int h_margin) {
        root.add(generateStatusOutputComponent(height, w_margin, h_margin), w_margin, y_row_index, GRID_X - w_margin, height);
    }

    private HBox generateStatusOutputComponent(int height, int w_margin, int h_margin) {

        HBox outputContainer = new HBox();

        outputContainer.setStyle("-fx-background-color: #" + WHITE_COLOR + ";");

        // generate label
        STATUS_OUTPUT.setStyle("-fx-padding: 0, 0, 0, 8;");
        STATUS_OUTPUT.setStyle("-fx-text-fill: #000000;");
        STATUS_OUTPUT.setAlignment(Pos.CENTER_LEFT);

        setStatus("Welcome to Cwacos!");

        // Add label to HBox
        outputContainer.getChildren().addAll(
                STATUS_OUTPUT
        );

        y_row_index += height;

        return outputContainer;
    }

    //============================== STYLE SETTERS =============================

    //Style the type drop down menu in the dialog window
    private void styleDropDownMenu(ComboBox<String> cb) {
        //CellFactory that styles each item in the drop down menu
        cb.setCellFactory(
                new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        final ListCell<String> cell = new ListCell<String>() {
                            @Override
                            public void updateItem(String item,
                                                   boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    //Style the cell
                                    setText(item);
                                    setTextFill(Paint.valueOf(WHITE_COLOR));
                                    setBackground(new Background(new BackgroundFill(Paint.valueOf(SECONDARY_COLOR), null, null)));
                                } else {

                                }
                            }
                        };
                        return cell;
                    }
                });

        //return cb;
    }

    //Meethod styles the dialog window
    private TextInputDialog styleChoiceDialog(TextInputDialog td) {
        td.getDialogPane().getContent().setStyle("-fx-background-color: #" + PRIMARY_COLOR + ";");
        td.getDialogPane().setStyle("-fx-background-color: #" + PRIMARY_COLOR + ";");
        td.setHeaderText(null);
        td.setGraphic(null);

        GridPane gp = (GridPane) td.getDialogPane().getContent();
        //Style the text field
        gp.getChildren().get(1).setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";"
                + "-fx-text-fill: #" + WHITE_COLOR + ";");
        //Style the type drop down menu
        gp.getChildren().get(0).setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");
        //Style the remove/add button
        gp.getChildren().get(2).setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";"
                + "-fx-text-fill: #" + WHITE_COLOR + ";");
        //Add padding between buttons
        gp.setHgap(10);

        td.getDialogPane().setContent(gp);
        return td;
    }

    //=============================== SETTERS ===================================

    public void setStatus(String msg) {
        STATUS_OUTPUT.setText(STATUS_LEAD + msg);
    }
}