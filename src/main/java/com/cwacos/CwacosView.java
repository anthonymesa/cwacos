package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * <p>
 * Purpose: CwacosView dynamically creates the UI and also defines UI actions that
 * run when user interacts with it. All non-UI data manipulation occurs in CwacosData.
 * <p>
 * Contributing Authors:
 *      Jack Fink
 *      Anthony Mesa
 *      Hyoungjin Choi
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import javafx.geometry.*;

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
    private static final String SAVE = "Save";
    private static final String CANCEL = "Cancel";
    private static final String ADD = "Add";
    private static final String REMOVE = "Remove";
    private static final String UPDATE = "Update";
    private static final String ADD_TITLE = "Add Stock/Crypto";
    private static final String REMOVE_TITLE = "Remove Stock/Crypto";

    /* Leading characters for status display */
    private static final String STATUS_LEAD = "  ~ ";

    /* Custom spacing values */
    private final int BUTTON_ICON_SIZE = 20;
    private final int BUTTON_SPACING = 2;
    private final int COMPONENT_SPACING = 10;
    private static final int ACTIVE_LABEL_WIDTH = 50;

    /* yRowIndex keeps track of the grid y index that the next
       row will be populated on when a new row is added to root. */
    private int yRowIndex = 0;

    //=========================================================================
    // VIEW SETUP
    //=========================================================================

    /**
     * Begins UI
     *
     * @param args Arguments for UI
     */
    public static void beginUI(String[] args) {
        launch(args);
    }

    /**
     * Set up the gridpane by setting the background color, and also setting the
     * column and row widths/heights based off of predefined grid x and y values
     * in reference to the view width and height.
     */
    private void setupGridPane() {
        root.setStyle("-fx-background-color: " + UIColors.getPrimaryColor() + ";");   //Background color

        for (int i = 0; i < GRID_X; i++) {
            root.getColumnConstraints().add(new ColumnConstraints(WINDOW_WIDTH / GRID_X));
        }

        for (int i = 0; i < GRID_Y; i++) {
            root.getRowConstraints().add(new RowConstraints(WINDOW_HEIGHT / GRID_Y));
        }
    }

    /**
     * Set view closing action to save state
     */
    @Override
    public void stop() {
        CwacosData.saveState();
    }

    //=========================================================================
    // VIEW BEGIN
    //=========================================================================

    /**
     * Starts the application by doing a handful of things:
     *
     * 1. Loads the previous state
     * 2. Initializes the popup class
     * 3. Shows the quakka facts dialogue
     * 4. Generates the main application view dynamically by rows
     * 5. Populates the menus in the view (requires all dynamic rows to be drawn first)
     * 6. Sets active data in cwacosData to the first available data (null or not)
     * 7.
     *
     * @param primaryStage Window to display main application
     */
    @Override
    public void start(Stage primaryStage) {

        /* This is called first from the UI so we can ensure settings have been loaded. */
        Response stateLoadResponse = CwacosData.loadState();

        /* Initialize static popup class to be used for quokka fact. */
        CwacosPopup.init();

        /* Show the Cwacos facts popup. */
        showCwacosFactsDialogue();

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
        setUiStatusLabel(stateLoadResponse.getStatus());

        /* Get root grid pane ready */
        setupGridPane();

        /* Create window and set scene. */
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setResizable(false);
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.getIcons().add(new Image("file:res/cwacos.jpg"));
        primaryStage.show();
    }

    /**
     * Dialogue box that shows up before the program begins.
     *
     * Makes call to getQuakkaFact to get a random fact.
     */
    private void showCwacosFactsDialogue() {

        /* Window content */
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);

        /* Placement box for image */
        HBox imgHolder = new HBox();
        imgHolder.setAlignment(Pos.CENTER);
        imgHolder.setPadding(new Insets(20,0,20,0));

        /* Image of mascot */
        ImageView view = new ImageView(new Image("file:res/cwacos.jpg"));
        view.setFitHeight(300);
        view.setPreserveRatio(true);

        imgHolder.getChildren().add(view);

        /* Add placement box to window */
        content.getChildren().addAll(imgHolder);

        /* Display random quokka fact with label */
        Label qfact = generateUILabel("Did you know? " + CwacosData.getQuakkaFact());
        qfact.setTextAlignment(TextAlignment.CENTER);
        qfact.setAlignment(Pos.CENTER);

        content.getChildren().add(qfact);

        CwacosPopup.display(APPLICATION_NAME, "Thanks!", content);
    }

    //=========================================================================
    // Generate Menu Bar
    //=========================================================================

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
        /* Add the button container to the top bar section of the GridPane */
        root.add(generateMenuBarComponentLeft(), w_margin, yRowIndex, (GRID_X / 2) - w_margin, height);
        root.add(generateMenuBarComponentRight(), (GRID_X / 2), yRowIndex, (GRID_X / 2), height);

        yRowIndex += height;
    }

    //=========================================================================
    // Generate Menu Bar Left
    //=========================================================================

    /**
     * Generate the left half of the menu bar
     */
    private HBox generateMenuBarComponentLeft() {
        HBox menuBar = new HBox();

        menuBar.setSpacing(COMPONENT_SPACING);

        /* Add buttons to menu bar hbox */
        menuBar.getChildren().addAll(
                generateSaveLoadOperationsComponent(),
                generateFavoritesOperationsComponent()
        );

        return menuBar;
    }

    /**
     * Genereates a container for the buttons required for
     * save/load operations
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
     * Generate a button for saving active data
     */
    public Button generateSaveActiveDataBtn() {

        Button saveBtn = new Button();

        ImageView view = new ImageView(new Image("file:res/save.png"));
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        saveBtn.setGraphic(view);

        saveBtn.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");
        saveBtn.setTooltip(new Tooltip("Save data to file"));

        saveBtn.setOnAction(getSaveActiveAction());

        return saveBtn;
    }

    /**
     * This is the handler to be applied to the save button.
     *
     * A file chooser dialogue is drawn to user and the file
     * url chosen is saved to the currently active data. The file
     * is then saved using the url chosen.
     */
    private EventHandler<ActionEvent> getSaveActiveAction() {
        return actionEvent -> {
            if (CwacosData.getActiveSymbol() == null) {
                setUiStatusLabel("There is no data to save...");
                return;
            }

            /* Create the file chooser that lets the user select the data the user wants to use */
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(SAVE);
            fileChooser.setInitialFileName(CwacosData.generateFileUrl());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All File", "*.*"));
            File selectedFile = fileChooser.showSaveDialog(new Stage());

            if (selectedFile != null) {
                CwacosData.setFileUrl(selectedFile.getAbsolutePath());
            }

            /* Save data related to given symbol to file at given url. */
            Response saveResponse = CwacosData.saveData();

            /* Set label to the output status that CwacosData gives back. */
            setUiStatusLabel(saveResponse.getStatus());
        };
    }

    /**
     * Generate the button for loading data into the view.
     *
     * @return Load file button
     */
    public Button generateLoadFileToActiveDataBtn() {

        Button loadBtn = new Button();

        ImageView view = new ImageView(new Image("file:res/folder.png"));
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        loadBtn.setGraphic(view);

        loadBtn.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");
        loadBtn.setTooltip(new Tooltip("Load data from file"));

        loadBtn.setOnAction(getLoadActiveAction());

        return loadBtn;
    }

    /**
     * This is the handler to be applied to the load button.
     *
     * A file chooser dialogue is drawn to user and the file
     * url chosen is loaded to the currently active data.
     */
    private EventHandler<ActionEvent> getLoadActiveAction() {
        return actionEvent -> {
            /* Create the file chooser that lets the user select the data the user wants to use. */
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(LOAD);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All File", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());

            String file_url = null;

            /* Get the file path to be loaded. */
            try {
                file_url = selectedFile.getAbsolutePath();
            } catch (Exception ex) {
                /* User exited or an issue occurred from the file chooser. */
                return;
            }

            /* Set up parameters for call to loadData. */
            ArrayList<String> load_parameters = new ArrayList<String>( Arrays.asList( file_url ));

            Response loadResponse = CwacosData.loadData(load_parameters);

            /* If response is bad, exit. */
            if (!loadResponse.getSuccess()) {
                setUiStatusLabel(loadResponse.getStatus());
                return;
            }

        /* Get the symbol and the type of loaded favorite
           so that it can be added to the favorites menu. */
            String symbol = CwacosData.getActiveSymbol();
            int type = CwacosData.getActiveType();

            generateFavoriteMenuItem(symbol, type);
            setUiStatusLabel(symbol + " loaded successfully!");
            updateView();
        };
    }

    /**
     * Generates the favorites operations component to be
     * added to the view.
     *
     * @return Favorite operations component
     */
    private HBox generateFavoritesOperationsComponent() {
        HBox favoritesContainer = new HBox();

        favoritesContainer.setAlignment(Pos.CENTER_LEFT);
        favoritesContainer.setSpacing(BUTTON_SPACING);

        /* Add buttons to "Update UpdateAll" hbox. */
        favoritesContainer.getChildren().addAll(
            generateAddFavoriteBtn(),
            generateRemoveFavoriteBtn(),
            generateStocksCryptoMenu(),
            generateActiveTicker()
        );

        return favoritesContainer;
    }

    /**
     * Generates the add favorite button
     *
     * @return Add favorite button
     */
    private Button generateAddFavoriteBtn() {
        Button addBtn = new Button();

        Image img = new Image("file:res/plus-square.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);

        addBtn.setGraphic(view);
        addBtn.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");
        addBtn.setTooltip(new Tooltip("Add favorite"));

        /* Set action for clicking 'add favorite'. */
        addBtn.setOnAction(getAddFavoriteAction());

        return addBtn;
    }

    private EventHandler<ActionEvent> getAddFavoriteAction() {
        return e -> {
            VBox content = new VBox();
            content.setAlignment(Pos.CENTER);

            popupLabel(content, "Symbol");

            /* Create text field that takes user input. */
            TextField inputArea = new TextField();
            inputArea.setPromptText("Enter ticker: ");
            inputArea.setMaxWidth(150);
            content.getChildren().add(inputArea);

            popupLabel(content, "type");

            /* Create the drop down menu for selecting which type of ticker you have. */
            ComboBox<String> typeSelection = generateTypeComboBox(content);

            /* define function to be run when user clicks 'okay'. */
            Function<Object, Object> testFunction = addFavoriteSuccessClicked(inputArea, typeSelection);

            /* call popup with above parameters. */
            CwacosPopup.display(ADD_TITLE, ADD, CANCEL, content, testFunction);
        };
    }

    /**
     * Defines the event that the 'okay' button is clicked in the addFavorite popup window.
     *
     * @param inputArea TextField for user input of symbol
     * @param typeSelection ComboBox for selecting type of data
     * @return Function to be abblied to window button
     */
    private Function<Object, Object> addFavoriteSuccessClicked(TextField inputArea, ComboBox<String> typeSelection) {
        Function<Object, Object> testFunction = o -> {

            /* Get symbol from input box and trim trailing and ending spaces. */
            String symbol = inputArea.getText().trim().toUpperCase();

            /* get int type based on user's selection index in comboBox. */
            int type = typeSelection.getSelectionModel().getSelectedIndex();

            /* validates type is actually selected from the dropdown. */
            if ((type < 0) || (symbol.length() == 0)) {
                return -1;
            } else {

                /* Add favorite and get response */
                Response addFavoriteResponse = CwacosData.addFavorite(symbol, type);

                /* Check if there was an error adding the favorite */
                if (!addFavoriteResponse.getSuccess()) {
                    setUiStatusLabel(addFavoriteResponse.getStatus());
                    return -1;
                }

                generateFavoriteMenuItem(symbol, type);

                /* Now that we have completed all necessary actions, we can print the success status */
                setUiStatusLabel(addFavoriteResponse.getStatus());

                // Set the active data to the newly added symbol
                CwacosData.setActiveSymbol(symbol);
                CwacosData.setActiveType(type);

                updateView();

                return 0;
            }
        };
        return testFunction;
    }

    /**
     * Generate the remove favorites button.
     *
     * @return Button to remove favorites
     */
    private Button generateRemoveFavoriteBtn() {
        Button removeFavBtn = new Button();

        ImageView view = new ImageView(new Image("file:res/minus-square.png"));
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        removeFavBtn.setGraphic(view);

        removeFavBtn.setStyle("-fx-border-width: 2px; ");
        removeFavBtn.setStyle("-fx-border-color: " + UIColors.getAccentColor() + ";");
        removeFavBtn.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");
        removeFavBtn.setTooltip(new Tooltip("Remove favorite"));

        removeFavBtn.setOnAction(getRemoveFavoriteEventHandler());

        return removeFavBtn;
    }

    /**
     * Gets generates the action to be run when remove favorites is clicked.
     *
     * @return Remove favorites action
     */
    private EventHandler<ActionEvent> getRemoveFavoriteEventHandler() {
        return e -> {
            VBox content = new VBox();
            content.setAlignment(Pos.CENTER);

            Label typeLabel = generateUILabel("Type");
            typeLabel.setPadding(new Insets(10, 0, 10, 0));
            content.getChildren().add(typeLabel);

            //Create the drop down menu for selecting which type of ticker you have
            ComboBox<String> typeSelection = generateTypeComboBox(content);

            Label inputLabel = generateUILabel("Symbol");
            inputLabel.setPadding(new Insets(10, 0, 10, 0));
            content.getChildren().add(inputLabel);

            //Create text field that takes user input
            TextField inputArea = new TextField();
            inputArea.setMaxWidth(150);
            inputArea.setPromptText("Enter ticker: ");
            content.getChildren().add(inputArea);

            Function<Object, Object> testFunction = removeFavoriteSuccessClicked(typeSelection, inputArea);

            CwacosPopup.display(REMOVE_TITLE, REMOVE, CANCEL, content, testFunction);
        };
    }

    /**
     * Define function for when success is clicked in remove favorite popup.
     *
     * @param typeSelection Combo Box for type selection
     * @param inputArea InputArea for symbol
     * @return Function to run on success
     */
    private Function<Object, Object> removeFavoriteSuccessClicked(ComboBox<String> typeSelection, TextField inputArea) {
        Function<Object, Object> testFunction = o -> {

            /* Get string from user input */
            String symbol = inputArea.getText().toUpperCase();

            /* Get the type the user selected */
            int type = typeSelection.getSelectionModel().getSelectedIndex();

            /* Check that combo box is selected and input has text */
            if ((type < 0) || (symbol.length() == 0)) {
                return -1;
            } else {

                /* Remove favorite and get response */
                Response removeFavoriteResponse = CwacosData.removeFavorite(symbol, type);

                /* Check if removal had errors */
                if (!removeFavoriteResponse.getSuccess()) {
                    setUiStatusLabel(removeFavoriteResponse.getStatus());
                    return -1;
                }

                /* Pick one of the type menus from the menu bar depending on ticker type selection */
                Menu menu = getFavoritesMenu().getMenus().get(type);

                /* Search the menu for the symbol to remove */
                for (int i = 0; i < menu.getItems().size(); i++) {

                    /* Store the current menu item that's being checked */
                    MenuItem current = menu.getItems().get(i);

                    /* Store the string contained in the menu item */
                    String text = current.getText();

                    /* Check if the ticker the user entered matches the current menu item */
                    if (text.contains(symbol)) {

                        /* Remove that ticker from the menu */
                        menu.getItems().remove(current);
                    }
                }

                /* Set success status */
                setUiStatusLabel(removeFavoriteResponse.getStatus());

                updateView();

                return 0;
            }
        };
        return testFunction;
    }

    /**
     * Create the stocks / crypto menu bar.
     * <p>
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

        /* Add to HBox layout to make layout look nicer. */
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

    /**
     * For all symbols created, create a menuitem item for the menu.
     *
     * @param _symbols Symbols to append to menu
     * @param _type Integer representing type of data
     */
    private void generateFavoriteMenuItems(ArrayList<String> _symbols, int _type) {
        for (String symbol : _symbols) {
            generateFavoriteMenuItem(symbol, _type);
        }
    }

    /**
     * Create item to be appended to favorites menu
     *
     * @param _symbol Symbol to be appended
     * @param _type Type of DataSegment
     */
    private void generateFavoriteMenuItem(String _symbol, int _type) {

        /* This checks that the favorite exists in the menu. If it does, it doesnt need.
           to create a new ticker. */
        for(MenuItem each : getFavoritesMenu().getMenus().get(_type).getItems()) {
            if (each.getText().equals(_symbol)) {
                return;
            }
        }

        MenuItem newFavorite = new MenuItem(_symbol);

        /* Set callback event for clicking menu item (the favorite itself). */
        newFavorite.setOnAction(onClickFavoriteAction(_symbol, _type));

        // Add menu item to the menu of the correct type in the menu bar.
        getFavoritesMenu().getMenus().get(_type).getItems().add(newFavorite);
    }

    /**
     * Action to run when a favorite is clicked in the favorites menu
     *
     * @param _symbol Symbol of data that is indicated by menu item
     * @param _type Type of data indidcated by menu item
     * @return An action that is run when favorites menu item is clicked
     */
    private EventHandler<ActionEvent> onClickFavoriteAction(String _symbol, int _type) {
        return actionEvent -> {
            /* Set the active symbol and type and update table contents. */
            CwacosData.setActiveSymbol(_symbol);
            CwacosData.setActiveType(_type);

            updateView();

            setUiStatusLabel("Now viewing " + _symbol);
        };
    }

    /**
     * Generate the active ticker display, showing the current data being viewed
     *
     * @return Hbox representing ticker object
     */
    private HBox generateActiveTicker() {
        HBox activeTicker = new HBox();
        activeTicker.setPadding(new Insets(COMPONENT_SPACING));

        HBox activeTickerBg = new HBox();

        activeTickerBg.setAlignment(Pos.CENTER);
        activeTickerBg.setMinWidth(ACTIVE_LABEL_WIDTH);

        activeTickerBg.setStyle("-fx-border-width: 2px; ");
        activeTickerBg.setStyle("-fx-border-color: " + UIColors.getAccentColor() + ";");
        activeTickerBg.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");

        Label activeTickerLabel = generateUILabel("");

        /* generate label. */
        activeTickerLabel.setId("activeTickerLabel");
        activeTickerLabel.setAlignment(Pos.CENTER_LEFT);

        /* Add buttons to menu bar hbox. */
        activeTickerBg.getChildren().add(activeTickerLabel);
        activeTicker.getChildren().add(activeTickerBg);

        return activeTicker;
    }

    //=========================================================================
    // Generate Menu Bar Right
    //=========================================================================

    /**
     * Generate right side of menu bar.
     *
     * @return Hbox for right side of menu bar
     */
    private HBox generateMenuBarComponentRight() {
        HBox menuBar = new HBox();

        menuBar.setAlignment(Pos.CENTER_RIGHT);
        menuBar.setSpacing(COMPONENT_SPACING);

        /* Add buttons to menu bar hbox. */
        menuBar.getChildren().addAll(
                generateMaxProfitComponent(),
                generateUpdateOperationsComponent()
        );

        return menuBar;
    }

    /**
     * Generate max profit component of menu
     *
     * @return Max profit HBox component
     */
    private HBox generateMaxProfitComponent() {

        HBox buttonContainer = new HBox();

        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setSpacing(BUTTON_SPACING);

        /* Add buttons to "Update UpdateAll" hbox. */
        buttonContainer.getChildren().addAll(
                generateMaxProfitButton()
        );

        return buttonContainer;
    }

    /**
     * Generate the max profit button.
     *
     * @return Button for max profit
     */
    private Button generateMaxProfitButton() {

        Button maxProfitBtn = new Button();

        ImageView view = new ImageView(new Image("file:res/dollar-sign.png"));
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        maxProfitBtn.setGraphic(view);

        maxProfitBtn.setStyle("-fx-border-width: 2px; ");
        maxProfitBtn.setStyle("-fx-border-color: " + UIColors.getAccentColor() + ";");
        maxProfitBtn.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");
        maxProfitBtn.setTooltip(new Tooltip("Run MaxProfit algorithm"));

        /* Set action for clicking 'add favorite'. */
        maxProfitBtn.setOnAction(getMaxProfitHandler());

        return maxProfitBtn;
    }

    /**
     * Generate the handler to be run when max profit is clicked.
     *
     * @return Action that should be run when max profit is clicked
     */
    private EventHandler<ActionEvent> getMaxProfitHandler() {
        return e -> {
            /* If table view is empty, return. */
            if (!CwacosData.dataAvailable()) {
                setUiStatusLabel("Max profit analysis requires data to be loaded...");
                return;
            }

            VBox content = new VBox();
            content.setAlignment(Pos.CENTER);

            String[] maxProfitData = CwacosData.getMaxProfit();

            //Create text field that takes user input
            Label lowDate = generateUILabel(maxProfitData[2]);
            lowDate.setPadding(new Insets(10, 0, 10, 0));

            Label low = generateUILabel("Low: $" + maxProfitData[0]);
            Label buy = generateUILabel("Buy: $" + maxProfitData[1]);

            Label highDate = generateUILabel(maxProfitData[5]);
            highDate.setPadding(new Insets(10, 0, 10, 0));

            Label high = generateUILabel("High: $" + maxProfitData[3]);
            Label sell = generateUILabel("Sell: $" + maxProfitData[4]);

            Label profit = generateUILabel("Profit: $" + maxProfitData[6]);
            profit.setPadding(new Insets(10, 0, 10, 0));

            content.getChildren().addAll(lowDate, low, buy, highDate, high, sell, profit);

            // call popup with above parameters
            CwacosPopup.display("Max Analysis", "Nice", content);
        };
    }

    /**
     * Creates HBox section for containing data update operations.
     *
     * @return Update operation container
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
     * Generate button for updating data in active view.
     *
     * @return Button for updating active data
     */
    public Button generateUpdateActiveDataBtn() {

        Button updateBtn = new Button();

        ImageView view = new ImageView(new Image("file:res/rotate-cw.png"));
        view.setFitHeight(BUTTON_ICON_SIZE);
        view.setPreserveRatio(true);
        updateBtn.setGraphic(view);

        updateBtn.setStyle("-fx-border-width: 2px; ");
        updateBtn.setStyle("-fx-border-color: " + UIColors.getAccentColor() + ";");
        updateBtn.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");
        updateBtn.setTooltip(new Tooltip("Update data for table in view"));

        /**
         * This is an anonymous function to set the handler for when the
         * update button is clicked in the main UI
         */
        updateBtn.setOnAction(getUpdateActiveHandler());

        return updateBtn;
    }

    /**
     * Get handler that should be called when updating active data.
     *
     * @return Event handler for updating active data
     */
    private EventHandler<ActionEvent> getUpdateActiveHandler() {
        return e -> {

            if (CwacosData.getActiveSymbol() == null) {
                setUiStatusLabel("There is no data to update...");
                return;
            }

            VBox content = new VBox();
            content.setAlignment(Pos.CENTER);

            Label typeLabel = generateUILabel("Type");
            typeLabel.setPadding(new Insets(10, 0, 10, 0));
            content.getChildren().add(typeLabel);

            // combo box for call type
            final ComboBox<String> callArgument1 = createComboBox();

            // Check that there is an active symbol, else there is nothing to update. Doing this here because
            // filling the combo box requires a valid active type.
            if (CwacosData.getActiveType() == -1) {
                setUiStatusLabel("Must be viewing data to be able to update...");
                return;
            }

            // Get the call types to display in dropdown
            String[] callTypes = CwacosData.getCallTypes();

            // Add call types to combo box
            for (int i = 0; i < callTypes.length; i++) {
                callArgument1.getItems().add(callTypes[i]);
            }

            // Add combo box to dialogue box.
            content.getChildren().add(callArgument1);

            Label intervalLabel = generateUILabel("");

            // Change the label for the second combobox based
            // on type.
            switch (CwacosData.getActiveType()) {
                case 0:
                    intervalLabel.setText("Interval");
                    break;
                case 1:
                    intervalLabel.setText("Market");
                    break;
            }

            intervalLabel.setPadding(new Insets(10, 0, 10, 0));
            content.getChildren().add(intervalLabel);

            final ComboBox<String> callArgument2 = createComboBox();

            // detect change of selection in callArgument1 and populate callArgument2 appropriately
            callArgument1.getSelectionModel().selectedItemProperty().addListener(getChangeListener(callArgument1, callArgument2));

            content.getChildren().add(callArgument2);

            Function<Object, Object> desiredAction = getUpdateSuccessFunction(callArgument1, callArgument2);

            // call popup with above parameters
            CwacosPopup.display(UPDATE, UPDATE, CANCEL, content, desiredAction);
        };
    }

    /**
     * This function is an anoymous function that defines the action that will
     * take place when the user clicks the "okay" button (or its equivalent)
     * in the dialogue popup generated from clicking the update button in the main UI.
     *
     * @param callArgument1
     * @param callArgument2
     * @return
     */
    private Function<Object, Object> getUpdateSuccessFunction(ComboBox<String> callArgument1, ComboBox<String> callArgument2) {
        Function<Object, Object> desiredAction = new Function<Object, Object>() {
            @Override
            public Object apply(Object o) {

                int arg1 = callArgument1.getSelectionModel().getSelectedIndex();
                int arg2 = callArgument2.getSelectionModel().getSelectedIndex();

                // check if selected item in argument 1 is Intraday
                if (callArgument1.getSelectionModel().getSelectedItem().equals("Intraday")) {
                    // skip the 1st index of call interval, which is None
                    arg2++;
                }

                // check that type and interval have been chosen
                if ((arg1 < 0) || (arg2 < 0) || (CwacosData.getActiveSymbol().equals(""))) {
                    return -1;
                } else {
                    Response updateResponse = CwacosData.update(arg1, arg2);

                    // if size of current entry list is zero, there was an error
                    if (!updateResponse.getSuccess()) {
                        setUiStatusLabel(updateResponse.getStatus());
                        return -1;
                    }

                    setUiStatusLabel(updateResponse.getStatus());

                    updateView();

                    return 0;
                }
            }
        };
        return desiredAction;
    }

    /**
     * This changes the state of combo box 2 depending on the selected state of combo box 1 in
     * the update popup view.
     *
     * @param callArgument1 combo box of call types
     * @param callArgument2 combo box of call intervals
     * @return Event handler to fire when new combo box menu item clicked
     */
    private ChangeListener<String> getChangeListener(ComboBox<String> callArgument1, ComboBox<String> callArgument2) {
        return (observableValue, s, t1) -> {
            switch (CwacosData.getActiveType()) {
                case 0: //stock
                    populateStockIntervalComboBox(callArgument2, callArgument1.getValue());
                    break;

                case 1: // cryptos
                    String[] cryptoMarkets = CwacosData.getCryptoMarkets();

                    for (int i = 0; i < cryptoMarkets.length; i++) {
                        callArgument2.getItems().add(cryptoMarkets[i]);
                    }
                    break;
            }
        };
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

        updateAllBtn.setStyle("-fx-border-width: 2px; ");
        updateAllBtn.setStyle("-fx-border-color: " + UIColors.getAccentColor() + ";");
        updateAllBtn.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");
        updateAllBtn.setTooltip(new Tooltip("Update all data"));

        updateAllBtn.setOnAction(getUpdateAllHandler());

        return updateAllBtn;
    }

    /**
     * Get Handler to be run when update all is clicked.
     *
     * @return EventHandler for update all action
     */
    private EventHandler<ActionEvent> getUpdateAllHandler() {
        return e -> {

            setUiStatusLabel("Updating all symbols, this will take approximately " + CwacosData.getUpdateWaitTimeAsString());

            if (CwacosData.getActiveSymbol() == null) {
                setUiStatusLabel("There is no data to update...");
                return;
            }

            /* update all takes some time and needs to run in a thread beside the ui */
            Task<String> updateData = getUpdateAllThread();

            /* We defined the thread above, now we can run it.*/
            Thread updateThread = new Thread(updateData);
            updateThread.start();
        };
    }

    /**
     * Defines the thread to be run when update all is called.
     *
     * @return Thread for updating all data
     */
    private Task<String> getUpdateAllThread() {
        Task<String> updateData = new Task<String>() {
            @Override
            public String call() {
                Response updateAllResponse = CwacosData.updateAll();

                /* Because we are in a thread, we can not affect the ui state
                   unless we call this and add to it what we want to change when
                   updating all is complete. */
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!updateAllResponse.getSuccess()) {
                            setUiStatusLabel(updateAllResponse.getStatus());
                        }

                        setUiStatusLabel(updateAllResponse.getStatus());

                        updateView();
                    }
                });

                return null;
            }
        };
        return updateData;
    }

    //=========================================================================
    // Generate Active Data Table
    //=========================================================================

    /**
     * Generate Component to hold data table stack pane and add it to root.
     *
     * @param height height of row in grid spaces
     * @param w_margin width of component margin in grid spaces
     * @param h_margin height of component margin in grid spaces
     */
    private void generateActiveDataTableComponent(int height, int w_margin, int h_margin) {

        StackPane activeTickerView = new StackPane();
        activeTickerView.setStyle("-fx-background-color: " + UIColors.getPrimaryColor() + ";");
        activeTickerView.getChildren().add(createTableView());

        root.add(activeTickerView, w_margin, yRowIndex, GRID_X - w_margin, height);

        yRowIndex += height;
    }

    /**
     * Creates and returns the TableView control
     *
     * @return TableView control
     */
    private TableView<Entry> createTableView() {

        TableView<Entry> table = new TableView<Entry>();
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

    //=========================================================================
    // Generate Candle Stick Graph
    //=========================================================================

    /**
     * Generate the candle stick graph to be displayed
     *
     * @param height height of graph in grid units
     * @param w_margin width margin of graph in grid units
     * @param h_margin heigh margin of graph in grid units
     */
    private void generateCandlestickGraphComponent(int height, int w_margin, int h_margin) {

        //Create and style the background.
        StackPane graphPane = new StackPane();
        //graphPane.setStyle("-fx-background-color: #" + UIColors.getAccentColor() + ";");    //Set background of the graph. valueOf converts a color hex code to a JavaFX Paint object.
        graphPane.setId("chart");

        //Create each axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Interval");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Price");

        //Create the LineChart
        LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

        lineChart.getStylesheets().add("file:res/style.css");

        //Create lines for the line chart
        XYChart.Series openSeries = new XYChart.Series();
        openSeries.setName("Open");
        XYChart.Series closeSeries = new XYChart.Series();
        closeSeries.setName("Close");
        XYChart.Series lowSeries = new XYChart.Series();
        lowSeries.setName("Low");
        XYChart.Series highSeries = new XYChart.Series();
        highSeries.setName("High");

        lineChart.getData().addAll(openSeries, closeSeries, lowSeries, highSeries);

        graphPane.getChildren().addAll(lineChart);

        root.add(graphPane, w_margin, yRowIndex, GRID_X - w_margin, height);

        yRowIndex += height;
    }

    /**
     * Get chart by lookup value.
     */
    private StackPane fetchChart() {
        return (StackPane) root.lookup("#chart");
    }

    /**
     * Populate chart dynamically based on data being actively viewed.
     */
    private void populateChart() {
        StackPane sp = fetchChart();
        LineChart<String, Number> lineChart = (LineChart<String, Number>) sp.getChildren().get(0);

        //Get ticker data.
        ArrayList<Entry> entries = CwacosData.getActiveEntryList();

        //Get the series for each of the lines.
        ObservableList<XYChart.Series<String, Number>> chartSeries = lineChart.getData();
        XYChart.Series<String, Number> openSeries = chartSeries.get(0);
        XYChart.Series<String, Number> closeSeries = chartSeries.get(1);
        XYChart.Series<String, Number> lowSeries = chartSeries.get(2);
        XYChart.Series<String, Number> highSeries = chartSeries.get(3);

        double highest = 0;
        double lowest = 0;

        //Clears the graph if there was something there before.
        if (openSeries.getData() != null) {
            openSeries.getData().clear();
            closeSeries.getData().clear();
            lowSeries.getData().clear();
            highSeries.getData().clear();
        }
        if (CwacosData.dataAvailable()) {
            //This is an invisible rectangle that will get rid of the dumb dots that represent points on the line graph.
            Rectangle rect = new Rectangle(0, 0);
            rect.setVisible(false);

            lineChart.setVisible(true);

            for (Entry entry : entries) {
                if (entries.indexOf(entry)==0) {
                    highest = entry.getHigh();
                    lowest = entry.getLow();
                }

                //Add the entry data with the rectangle to their respective lines.
                openSeries.getData().add(new XYChart.Data(entry.getGraphTimeString(), entry.getOpen()));
                openSeries.getData().get(entries.indexOf(entry)).setNode(rect);
                closeSeries.getData().add(new XYChart.Data(entry.getGraphTimeString(), entry.getClose()));
                closeSeries.getData().get(entries.indexOf(entry)).setNode(rect);
                lowSeries.getData().add(new XYChart.Data(entry.getGraphTimeString(), entry.getLow()));
                lowSeries.getData().get(entries.indexOf(entry)).setNode(rect);
                highSeries.getData().add(new XYChart.Data(entry.getGraphTimeString(), entry.getHigh()));
                highSeries.getData().get(entries.indexOf(entry)).setNode(rect);

                //Checks if the current data point is the new highest/lowest of the data set.
                if (entry.getHigh() > highest) {
                    highest = entry.getHigh();
                }
                if (entry.getLow() < lowest) {
                    lowest = entry.getLow();
                }
            }
        } else {
            openSeries.getData().clear();
            closeSeries.getData().clear();
            lowSeries.getData().clear();
            highSeries.getData().clear();
            lineChart.setVisible(false);
        }

        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
        xAxis.setAnimated(false);
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(highest);
        yAxis.setLowerBound(lowest);
    }

    //=========================================================================
    // Generate Status Bar
    //=========================================================================

    /**
     * Generate bottom bar for status output
     */
    private void generateStatusBar(int height, int w_margin, int h_margin) {
        root.add(generateStatusOutputComponent(height, w_margin, h_margin), w_margin, yRowIndex, GRID_X - w_margin, height);
    }

    /**
     * Generate status bar component
     *
     * @param height
     * @param w_margin
     * @param h_margin
     * @return
     */
    private HBox generateStatusOutputComponent(int height, int w_margin, int h_margin) {

        HBox outputContainer = new HBox();

        outputContainer.setStyle("-fx-background-color: " + UIColors.getSecondaryColor() + ";");

        Label statusOutput = new Label();

        // generate label
        statusOutput.setId("statusOutput");
        statusOutput.setStyle("-fx-text-fill: " + UIColors.getFontColor() + ";");
        statusOutput.setPadding(new Insets(COMPONENT_SPACING/2,0,COMPONENT_SPACING/2,0));
        statusOutput.setAlignment(Pos.CENTER_LEFT);

        // Add label to HBox
        outputContainer.getChildren().addAll(
                statusOutput
        );

        yRowIndex += height;

        return outputContainer;
    }

    //=========================================================================
    // General helper functions for the view
    //=========================================================================

    /**
     * Populates the table with entries from the API.
     */
    private void populateTable() {

        TableView<Entry> table = fetchTable();

        // clear table entries if there are any
        if (!isTableEmpty(table)) {
            table.getItems().clear();
            table.refresh();
        }

        // populate the table
        ArrayList<Entry> entries = CwacosData.getActiveEntryList();

        if (entries != null) {
            for (Entry entry : entries) {
                table.getItems().add(entry);
            }
        }
    }

    /**
     * Checks if the data table is empty.
     *
     * @param _table Data table to be checked.
     * @return True if it is, false if it is not.
     */
    private boolean isTableEmpty(TableView<Entry> _table) {
        return _table.getItems().isEmpty();
    }

    /**
     * Fetches data table based on given ID.
     *
     * @return Fetched data table.
     */
    private TableView<Entry> fetchTable() {
        return (TableView<Entry>) root.lookup("#dataTable");
    }

    /**
     * Creates a combo box
     *
     * @return Combo Box with a prompt text.
     */
    private ComboBox<String> createComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Select...");
        return comboBox;
    }

    /**
     * Populates the stock interval combo box and the stock interval combo box only based
     * on the call type combo box selection.
     *
     * @param _comboBox Stock interval combo box.
     */
    private void populateStockIntervalComboBox(ComboBox<String> _comboBox, String _type) {

        // clear the combo box if there are items in it
        if (!_comboBox.getItems().isEmpty()) {
            _comboBox.getItems().clear();
        }

        String[] stockIntervals = CwacosData.getStockIntervals();

        // skip the first element of stockIntervals for intraday calls, which is "none"
        int k = 0;
        if (_type.equals("Intraday")) {
            k++;
        }

        for (int i = k; i < stockIntervals.length; i++) {

            // for any type of call besides Intraday call, populate the combo box with "none"
            // which is the first element of stockIntervals
            if (!_type.equals("Intraday")) {
                _comboBox.getItems().add(stockIntervals[i]);
                break;
            } else {
                _comboBox.getItems().add(stockIntervals[i]);
            }
        }
    }

    /**
     * Effectively used as a refresh function. Refreshes data-based view components.
     */
    private void updateView() {
        populateTable();
        populateChart();
        setTicker();
    }

    /**
     * Sets the UI status for the view
     *
     * @param msg
     */
    public void setUiStatusLabel(String msg) {
        ((Label) root.lookup("#statusOutput")).setText(STATUS_LEAD + msg);
    }

    /**
     * Set the ticker to the symbol of the data being viewed.
     */
    private void setTicker() {
        ((Label) root.lookup("#activeTickerLabel")).setText(CwacosData.getActiveSymbol());
    }

    /**
     * Get the favorites menu via lookup.
     * @return
     */
    private MenuBar getFavoritesMenu() {
        return (MenuBar) root.lookup("#favoritesMenu");
    }

    /**
     * Generate a combo box for selecting type of DataSegment.
     *
     * @param content
     * @return
     */
    private ComboBox<String> generateTypeComboBox(VBox content) {
        ComboBox<String> typeSelection = createComboBox();
        typeSelection.getItems().addAll("Stocks", "Cryptos");
        content.getChildren().add(typeSelection);
        return typeSelection;
    }

    /**
     * Generate a popup label to display any text
     *
     * @param content
     * @param _text
     */
    private void popupLabel(VBox content, String _text) {
        Label typeLabel = generateUILabel(_text);
        typeLabel.setPadding(new Insets(10,0,10,0));
        content.getChildren().add(typeLabel);
    };

    /**
     * Generate a UI specific label that is common throught the UI.
     *
     * @param _input
     * @return
     */
    public Label generateUILabel(String _input) {
        Label label = new Label(_input);
        label.setStyle("-fx-text-fill: " + UIColors.getFontColor() + ";");
        return label;
    }
}