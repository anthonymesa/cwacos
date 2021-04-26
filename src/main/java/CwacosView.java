/*
Last updated:
Purpose of this class:
Contributing Authors: Jack Fink, Anthony Mesa, Hyoungjin Choi
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

    /* Window dimensions*/
    private static final int WINDOW_WIDTH = 1465;
    private static final int WINDOW_HEIGHT = 1000;

    /* Grid pane root for Window */
    private GridPane root = new GridPane();

    /* Window grid dimensions */
    private static final int GRID_X = 60;
    private static final int GRID_Y = 60;

    /* UI Labels */
    private static final String APPLICATION_NAME = "Cwacos";
    private static final String LOAD = "Load";
    private static final String CANCEL = "Cancel";
    private static final String ADD = "Add";
    private static final String REMOVE = "Remove";
    private static final String UPDATE = "Update";
    private static final String ADD_TITLE = "Add Stock/Crypto";
    private static final String REMOVE_TITLE = "Remove Stock/Crypto";

    /* UI Colors */
    private static final String PRIMARY_COLOR = "1D1D1D";
    private static final String SECONDARY_COLOR = "4E4E4E";
    private static final String WHITE_COLOR = "FFFFFF";
    private static final String ACCENT_COLOR = "BB86FC";

    /* Leading characters for status display */
    private static final String STATUS_LEAD = "  ~ ";

    /* Custom spacing values */
    private final int BUTTON_ICON_SIZE = 20;
    private final int BUTTON_SPACING = 2;
    private final int COMPONENT_SPACING = 10;

    /* yRowIndex keeps track of the grid y index that the next
       row will be populated on when a new row is added to root. */
    private int yRowIndex = 0;

    /**
     * Begin UI
     * @param args Arguments for UI
     */
    public static void beginUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        /* This is called first from the UI so we can ensure settings have been loaded. */
        Response stateLoadResponse = CwacosData.loadState();

        /* Initialize static popup class to be used for quokka fact. */
        CwacosPopup.init();

        VBox content = new VBox();

        /* Display random quokka fact */
        Label qfact = new Label(CwacosData.getQuakkaFact());
        content.getChildren().add(qfact);
        CwacosPopup.display(APPLICATION_NAME, "Thanks!", content);

        /* Dynamically create all elements of the UI.
           This is done in rows, and because we are using the yRowIndex variable,
           to interchange the rows, just swap the order of the
           generation calls. */
        generateMenuBar(3, 1, 0);
        generateActiveDataTableComponent(32, 1, 0);
        generateCandlestickGraphComponent(24, 1, 1);
        generateStatusBar(1, 1, 0);

        /* Populate the menuBar with the items loaded from settings. */
        populateStocksMenu();
        populateCryptosMenu();

        /* Set the currently active element to the first available. */
        CwacosData.setNextActiveData();

        updateView();

        /* Set status based on state load. */
        setStatus(stateLoadResponse.status);

        /* Get root grid pane ready */
        setupGridPane();

        /* Create window and set scene. */
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setResizable(false);
        primaryStage.setTitle(APPLICATION_NAME);
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

        for (int i = 0; i < GRID_X; i++) {
            root.getColumnConstraints().add(new ColumnConstraints(WINDOW_WIDTH / GRID_X));
        }

        for (int i = 0; i < GRID_Y; i++) {
            root.getRowConstraints().add(new RowConstraints(WINDOW_HEIGHT / GRID_Y));
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
        root.add(generateMenuBarComponentLeft(height, w_margin, h_margin), w_margin, yRowIndex, (GRID_X / 2) - w_margin, height);

        // There is an odd thing going on here, technically the width should be GRID_X / 2 - (w_margin / 2) but it isn't and I don't know why.
        root.add(generateMenuBarComponentRight(height, w_margin, h_margin), (GRID_X / 2), yRowIndex, (GRID_X / 2), height);

        // update the y_index for the next row
        yRowIndex += height;
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
                Response saveResponse = CwacosData.saveData();

                // Set label to the output status that CwacosData gives back.
                setStatus(saveResponse.status);
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

                        // do nothing if the file input is empty
                        if(file_url.length() == 0){
                            return -1;
                        }

                        ArrayList<String> load_parameters = new ArrayList<String>(
                                Arrays.asList(
                                        file_url
                                )
                        );

                        Response loadResponse = CwacosData.loadData(load_parameters);

                        if(!loadResponse.success) {
                            setStatus(loadResponse.status);
                            return -1;
                        }

                        // This may seem redundant but this is so that in the action handler down
                        // below, there isn't a logic error. (setting from a getter from the same class)
                        String symbol = CwacosData.getActiveSymbol();
                        int type = CwacosData.getActiveType();

                        generateFavoriteMenuItem(symbol, type);

                        updateView();

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
                generateStocksCryptoMenu(),
                generateActiveTicker()
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

                        // Get symbol from input box and trim trailing and ending spaces.
                        String symbol = inputArea.getText().trim().toUpperCase();

                        // get int type based on users's selection index in comboBox.
                        int type = typeSelection.getSelectionModel().getSelectedIndex();


                        // validates type is actually selected from the dropdown
                        if((type < 0) || (symbol.length() == 0)) {
                            return -1;
                        } else {

                            // Add favorite and get response
                            Response addFavoriteResponse = CwacosData.addFavorite(symbol, type);

                            // Check if there was an error adding the favorite
                            if (!addFavoriteResponse.success) {
                                setStatus(addFavoriteResponse.status);
                                return -1;
                            }

                            generateFavoriteMenuItem(symbol, type);

                            // Now that we have completed all necessary actions, we can print the success status
                            setStatus(addFavoriteResponse.status);

                            // Set the active data to the newly added symbol
                            CwacosData.setActiveSymbol(symbol);
                            CwacosData.setActiveType(type);

                            updateView();

                            return 0;
                        }
                    }
                };

                // call popup with above parameters
                CwacosPopup.display(ADD_TITLE, ADD, CANCEL, content, testFunction);
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

                        // Check that combo box is selected and input has text
                        if((type < 0) || (symbol.length() == 0)){
                            return -1;
                        } else {

                            // Remove favorite and get response
                            Response removeFavoriteResponse = CwacosData.removeFavorite(symbol, type);

                            // Check if removal had errors
                            if (!removeFavoriteResponse.success) {
                                setStatus(removeFavoriteResponse.status);
                                return -1;
                            }

                            // Pick one of the type menus from the menu bar depending on ticker type selection
                            Menu menu = getFavoritesMenu().getMenus().get(type);

                            // Search the menu for the symbol to remove
                            for (int i = 0; i < menu.getItems().size(); i++) {

                                //Store the current menu item that's being checked
                                MenuItem current = menu.getItems().get(i);

                                //Store the string contained in the menu item
                                String text = current.getText();

                                //Check if the ticker the user entered matches the current menu item
                                if (text.contains(symbol)) {

                                    //Remove that ticker from the menu
                                    menu.getItems().remove(current);
                                }
                            }

                            // Set success status
                            setStatus(removeFavoriteResponse.status);

                            updateView();

                            return 0;
                        }
                    }
                };

                CwacosPopup.display(REMOVE_TITLE, REMOVE, CANCEL, content, testFunction);
            }
        });

        return saveBtn;
    }

    /**
     * Create the stocks / crypto menu bar.
     *
     * Because this function is run on load, after the state
     * has been loaded, this function will check if there are any
     * elements in the relevant data maps in CwacosData and add
     * them to their respective menus before adding it to the view.
     *
     * @return
     */
    private HBox generateStocksCryptoMenu() {

        MenuBar favoritesMenu = new MenuBar();
        favoritesMenu.setId("favoritesMenu");

        /* Create the menus to be added to the main menu bar. */
        Menu stocks = new Menu("Stocks");
        Menu cryptos = new Menu("Cryptos");

        favoritesMenu.getMenus().addAll(stocks, cryptos);

        //Add to HBox layout to make layout look nicer
        HBox middleOptionsLayout = new HBox();
        middleOptionsLayout.getChildren().add(favoritesMenu);
        middleOptionsLayout.setAlignment(Pos.CENTER_LEFT);

        return middleOptionsLayout;
    }

    /**
     * Iterate across CryptoData.stocksData to populate menu entries
     */
    private void populateStocksMenu() {

        final int type = 0;
        ArrayList<String> symbols = CwacosData.getStockSymbols();

        generateFavoriteMenuItems(symbols, type);
    }

    /**
     * Iterate across CryptoData.cryptoData to populate menu entries
     */
    private void populateCryptosMenu() {

        final int type = 1;
        ArrayList<String> symbols = CwacosData.getCryptoSymbols();

        generateFavoriteMenuItems(symbols, type);
    }

    private void generateFavoriteMenuItems(ArrayList<String> _symbols, int _type) {
        for(String symbol : _symbols) {
            generateFavoriteMenuItem(symbol, _type);
        }
    }

    private void generateFavoriteMenuItem(String _symbol, int _type) {
        MenuItem newFavorite = new MenuItem(_symbol);

        // Set callback event for clicking menu item (the favorite itself)
        newFavorite.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Set the active symbol and type and update table contents
                CwacosData.setActiveSymbol(_symbol);
                CwacosData.setActiveType(_type);

                updateView();

                setStatus("Now viewing " + _symbol);
            }
        });

        // Add menu item to the menu of the correct type in the menu bar
        getFavoritesMenu().getMenus().get(_type).getItems().add(newFavorite);
    }

    private HBox generateActiveTicker() {
        HBox activeTicker = new HBox();

        activeTicker.setAlignment(Pos.CENTER_LEFT);

        activeTicker.setStyle("-fx-padding: 30, 0, 0, 0;");
        activeTicker.setStyle("-fx-border-width: 0px; ");
        activeTicker.setStyle("-fx-border-color: #" + SECONDARY_COLOR + ";");
        activeTicker.setStyle("-fx-background-color: #" + SECONDARY_COLOR + ";");

        Label activeTickerLabel = new Label("");

        // generate label
        activeTickerLabel.setId("activeTickerLabel");
        activeTickerLabel.setStyle("-fx-label-padding: 30, 0, 0, 0;");
        activeTickerLabel.setStyle("-fx-text-fill: #FFFFFF;");
        activeTickerLabel.setAlignment(Pos.CENTER_LEFT);

        // Add buttons to menu bar hbox
        activeTicker.getChildren().addAll(
                activeTickerLabel
        );

        return activeTicker;
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

                String[] maxProfitData = CwacosData.getMaxProfit();

                //Create text field that takes user input
                Label lowDate = new Label(maxProfitData[2]);
                Label low = new Label("Lowest price was: " + maxProfitData[0]);
                Label buy = new Label("Best buy price was: " + maxProfitData[1]);

                Label highDate = new Label(maxProfitData[5]);
                Label high = new Label("Highest was : " + maxProfitData[3]);
                Label sell = new Label("Sell price was: " + maxProfitData[4]);

                Label profit = new Label("Profit was: " + maxProfitData[6]);
                content.getChildren().addAll(lowDate, low, buy, highDate, high, sell, profit);

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

        /**
         * This is an anonymous function to set the handler for when the
         * update button is clicked in the main UI
         */
        updateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                HBox content = new HBox();
                GridPane dialogContent = new GridPane();

                // combo box for call type
                // this could be done programmatically, technically, but we may not always use enums given the API we use,
                // so this will be done custom
                final ComboBox<String> callArgument1 = createComboBox();

                // Check that there is an active symbol, else there is nothing to update. Doing this here because
                // filling the combo box requires a valid active type.
                if(CwacosData.getActiveType() == -1){
                    setStatus("Must be viewing data to be able to update...");
                    return;
                }

                // Get the call types to display in dropdown
                String[] callTypes = CwacosData.getCallTypes();

                // Add call types to combo box
                for(int i = 0; i < callTypes.length; i++) {
                    callArgument1.getItems().add(callTypes[i]);
                }

                // Add combo box to dialogue box.
                dialogContent.add(callArgument1, 0, 0);

                final ComboBox<String> callArgument2 = createComboBox();

                switch(CwacosData.getActiveType()) {
                    case 0: //stock
                        String[] stockIntervals = CwacosData.getStockIntervals();

                        for(int i = 0; i < stockIntervals.length; i++) {
                            callArgument2.getItems().add(stockIntervals[i]);
                        }
                        break;

                    case 1: // cryptos
                        String[] cryptoMarkets = CwacosData.getCryptoMarkets();

                        for(int i = 0; i < cryptoMarkets.length; i++) {
                            callArgument2.getItems().add(cryptoMarkets[i]);
                        }
                        break;
                }

                dialogContent.add(callArgument2, 0, 1);

                /**
                 * This function is an anoymous function that defines the action that will
                 * take place when the user clicks the "okay" button (or its equivalent)
                 * in the dialogue popup generated from clicking the update button in the main UI.
                 */
                Function<Object, Object> desiredAction = new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {

                        int arg1 = callArgument1.getSelectionModel().getSelectedIndex();
                        int arg2 = callArgument2.getSelectionModel().getSelectedIndex();

                        // check that type and interval have been chosen
                        if((arg1 < 0) || (arg2 < 0) || (CwacosData.getActiveSymbol() == ""))
                        {
                            return -1;
                        } else {
                            Response updateResponse = CwacosData.update(arg1, arg2);

                            // if size of current entry list is zero, there was an error
                            if (!updateResponse.success) {
                                setStatus(updateResponse.status);
                                return -1;
                            }

                            setStatus(updateResponse.status);

                            updateView();

                            return 0;
                        }
                    }
                };

                content.getChildren().add(dialogContent);

                // call popup with above parameters
                CwacosPopup.display(UPDATE, UPDATE, CANCEL, content, desiredAction);
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
                setStatus("Updating all symbols, this will take approximately " + CwacosData.getUpdateWaitTimeAsString());

                if(CwacosData.getActiveSymbol() == null) {
                    setStatus("There is no data to update...");
                    return;
                }

                var responseWrapper = new Object() {
                    Response wrappedResponse;
                };

                Task updateData = new Task<>() {
                    @Override
                    public String call(){
                        responseWrapper.wrappedResponse = CwacosData.updateAll();

                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                if(!responseWrapper.wrappedResponse.success) {
                                    setStatus(responseWrapper.wrappedResponse.status);
                                }

                                setStatus(responseWrapper.wrappedResponse.status);

                                updateView();
                            }
                        });

                        return null;
                    }
                };

                Thread updateThread = new Thread(updateData);
                updateThread.start();
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

        root.add(activeTickerView, w_margin, yRowIndex, GRID_X - w_margin, height);

        yRowIndex += height;
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

        TableView table = new TableView();
        table.setId("dataTable");

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
        table.getColumns().setAll(openColumn, closeColumn, lowColumn, highColumn, volumeColumn, dateColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    /**
     * Populates the table with entries from the API.
     */
    private void populateTable() {

        TableView table = (TableView) root.lookup("#dataTable");

        // clear table entries if there are any
        if (!table.getItems().isEmpty()) {
            table.getItems().clear();
            table.refresh();
        }

        // populate the table
        ArrayList<Entry> entries = CwacosData.getActiveEntryList();

        if(entries != null) {
            for (Entry entry : entries) {
                table.getItems().add(entry);
            }
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

        tempBox.setHeight((WINDOW_HEIGHT / GRID_Y) * (height - (h_margin * 2)));
        tempBox.setWidth((WINDOW_WIDTH / GRID_X) * (GRID_X - w_margin));

        Label candlestick = new Label();
        candlestick.setText("Candlestick graph goes here.");
        candlestick.setStyle("-fx-background-color: #" + ACCENT_COLOR + ";");

        candlestickGraph.getChildren().addAll(tempBox, candlestick);
        candlestickGraph.setAlignment(tempBox, Pos.CENTER);

        root.add(candlestickGraph, w_margin, yRowIndex, GRID_X - w_margin, height);

        yRowIndex += height;
    }

    //============================= STATUS BAR =================================

    /**
     * Generate bottom bar for status output
     */
    private void generateStatusBar(int height, int w_margin, int h_margin) {
        root.add(generateStatusOutputComponent(height, w_margin, h_margin), w_margin, yRowIndex, GRID_X - w_margin, height);
    }

    private HBox generateStatusOutputComponent(int height, int w_margin, int h_margin) {

        HBox outputContainer = new HBox();

        outputContainer.setStyle("-fx-background-color: #" + WHITE_COLOR + ";");

        Label statusOutput = new Label();

        // generate label
        statusOutput.setId("statusOutput");
        statusOutput.setStyle("-fx-padding: 0, 0, 0, 8;");
        statusOutput.setStyle("-fx-text-fill: #000000;");
        statusOutput.setAlignment(Pos.CENTER_LEFT);

        // Add label to HBox
        outputContainer.getChildren().addAll(
            statusOutput
        );

        yRowIndex += height;

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

    private void updateView() {
        populateTable();
        setTicker();
    }

    public void setStatus(String msg) {
        ((Label) root.lookup("#statusOutput")).setText(STATUS_LEAD + msg);
    }

    private void setTicker() {
        ((Label) root.lookup("#activeTickerLabel")).setText(CwacosData.getActiveSymbol());
    }

    private MenuBar getFavoritesMenu() {
        return (MenuBar) root.lookup("#favoritesMenu");
    }
}