package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: CwacosData provides a central hub to access all of the data and data modifying functions
 *      within Cwacos. Any state or data modifications should be made using calls to static CwacosData functions.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 *      Michael Leonard
 *      Hyoungjin Choi
 *      Jack Fink
 */

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CwacosData {

    private static Map<String, String> settings = new HashMap<String, String>();
    private static final String SETTINGS_URL = "./res/cwacossettings.conf";

    public static final String TEMP_FOLDER = "tmp";

    private static String activeSymbol = null;
    private static int activeType = -1;

    private static final int UPDATE_WAIT_SECONDS = 13;

    // This map represents a mapped list of favorite data
    // Map of strings to Ticker objects
    // e.g. "GME" --> new Ticker(s, t, d)
    private static Map<String, StockDataSegment> stockData = new HashMap<String, StockDataSegment>();
    private static Map<String, CryptoDataSegment> cryptoData = new HashMap<String, CryptoDataSegment>();

    /**
     * Initializes static datastorage and datasources
     */
    public static void initDataSources(){
        DataStorage.init();
        Stocks.init();
        Cryptos.init();
        Qfacts.init();
    }

    //=========================================================================
    // Loading and Saving State
    //=========================================================================

    /**
     * Load the state of the software from local files and initialize data sources
     */
    public static Response loadState() {
        CwacosData.initDataSources();
        loadSettings();
        loadQuokkaFacts();

        // Check that settings has favorites and file locations
        if((settings.get("favorites") != null) && (settings.get("fileLocations") != null)) {

            // Parse the symbols and the file locations from the strings loaded into settings map
            String[] symbols = settings.get("favorites").split("\\|", 0);
            String[] fileLocations = settings.get("fileLocations").split("\\|", 0);

            // Check that there are an equal amount of symbols and file locations
            if ((symbols.length == fileLocations.length) && (symbols.length != 0)) {
                for (int i = 0; i < symbols.length; i++) {

                    ArrayList<String> load_parameters = new ArrayList<String>(
                        Arrays.asList(
                            fileLocations[i]
                        )
                    );

                    // after this is run once, activeSymbol and activeType are set
                    Response loadResponse = CwacosData.loadData(load_parameters);

                    if(!symbols[i].equals(activeSymbol)){
                        return new Response("Symbol in settings does not match symbol in file... Aborting load state", false);
                    }

                    if(!loadResponse.getSuccess()) {
                        return new Response("An error occurred while trying to load state...", false);
                    }
                }
                return new Response("Loading previous session. Welcome to Cwacos!", true);
            }
            return new Response("Startup settings are corrupt. Prior state not loaded", false);
        }
        return new Response("Starting new session. Welcome to Cwacos!", false);
    }

    /**
     * Saves the information state of the application. Supposed to take
     * place on close, should be one of the last things to do.
     */
    public static void saveState() {

        StringBuilder list_symbols = new StringBuilder();
        StringBuilder list_file_urls = new StringBuilder();

        saveAllStocks();
        saveAllCryptos();

        appendSettingsStrings(list_symbols, list_file_urls);

        settings.put("favorites", list_symbols.toString());
        settings.put("fileLocations", list_file_urls.toString());

        saveSettings();
    }

    //=========================================================================
    // Settings Manipulation
    //=========================================================================

    /**
     * Loads the settings from local files
     */
    public static void loadSettings() {

        ArrayList<String> load_parameters = new ArrayList<String>(
                Arrays.asList(
                        SETTINGS_URL
                )
        );

        settings = DataStorage.loadSettings(load_parameters);
    }

    /**
     * This is only called at the end of saveState, so the settings should have
     * been updated already.
     */
    private static void saveSettings() {
        ArrayList<String> saveParameters = new ArrayList<String>(
                Arrays.asList(
                        SETTINGS_URL
                )
        );

        DataStorage.saveSettings(settings, saveParameters);
    }

    /**
     * Build the strings for the settings object
     */
    private static void appendSettingsStrings(StringBuilder _symbols, StringBuilder _urls) {
        appendStocksSettings(_symbols, _urls);
        appendCryptoSettings(_symbols, _urls);
    }

    /**
     * Iterate through all the stock data segments in the stocks map and save
     * based on their symbols
     */
    private static void appendStocksSettings(StringBuilder _symbols, StringBuilder _urls){
        // gather state info to save for each element in the financeData
        for (Map.Entry<String, StockDataSegment> entry : stockData.entrySet()) {

            /* Only save to settings if file has been saved */
            if(entry.getValue().getFileUrl() != null) {
                _symbols.append(entry.getKey() + "|");
                _urls.append(entry.getValue().getFileUrl() + "|");
            }
        }
    }

    /**
     * Iterate through all the crypto data segments in the cryptos map and save
     * based on their symbols
     */
    private static void appendCryptoSettings(StringBuilder _symbols, StringBuilder _urls) {
        // gather state info to save for each element in the financeData
        for (Map.Entry<String, CryptoDataSegment> entry : cryptoData.entrySet()) {
            if(entry.getValue().getFileUrl() != null) {
                _symbols.append(entry.getKey() + "|");
                _urls.append(entry.getValue().getFileUrl() + "|");
            }
        }
    }

    //=========================================================================
    // Saving and Loading functions
    //=========================================================================

    /**
     * Iterate through all the stock data segments in the stocks map and save
     * based on their symbols
     */
    private static void saveAllStocks() {
        // these are kept separate because you cant concurrently modify
        for (Map.Entry<String, StockDataSegment> entry : stockData.entrySet()) {
            activeSymbol = entry.getKey();
            activeType = entry.getValue().getCallType();
            saveData();
        }
    }

    /**
     * Iterate through all the crypto data segments in the stocks map and save
     * based on their symbols
     */
    private static void saveAllCryptos() {
        // these are kept separate because you cant concurrently modify
        for (Map.Entry<String, CryptoDataSegment> entry : cryptoData.entrySet()) {
            activeSymbol = entry.getKey();
            activeType = entry.getValue().getCallType();
            saveData();
        }
    }

    /**
     * Save the stock symbol's associated entry data to a file by
     * looking up that stock symbol in the finance data map and
     * saving its data attribute of ArrayList<Entry> type.
     *
     */
    public static Response saveData() {

        if (!dataAvailable()) {
            return new Response("There is no data to save...", false);
        }

        String file_url = getFileUrl();

        // check if the url is null
        if (file_url == null) {
            file_url = generateDefaultUrl();
            setFileUrl(file_url);
            System.out.println(file_url);
        }

        // create list of save parameters to send to save function
        ArrayList<String> save_parameters = new ArrayList<String>(
                Arrays.asList(
                        file_url,
                        activeSymbol,
                        Integer.toString(activeType)
                )
        );

        ArrayList<Entry> data_to_save = getActiveEntryList();

        Response saveResponse = DataStorage.save(save_parameters, data_to_save);

        if(!saveResponse.getSuccess()) {
            return saveResponse;
        }

        // here we are customizing the response message a little but
        return new Response(getActiveSymbol() + saveResponse.getStatus(), saveResponse.getSuccess());
    }

    /**
     * Load the file to populate the data for the current symbol in the finance data map
     * <p>
     * Data can only be loaded for stocks/cryptos that have already been added to the finance data map.
     *
     * @param _params an array list containing the parameters
     *                for the current method of saving data.
     *                <p>
     *                current method is local file storage so _params should
     *                only contain a file url
     */
    public static Response loadData(ArrayList<String> _params) {

        LoadData loadInfo;

        // Using a try catch so we can create a Response object given that
        // the load function returns a data object. We have to get our
        // errors from somewhere...
        try {
            loadInfo = DataStorage.load(_params);
        } catch (Exception e){
            return new Response(e.toString(), false);
        }

        if(loadInfo.getData().size() == 0) {
            return new Response("Data loaded is empty or corrupt...", false);
        }

        activeSymbol = loadInfo.getSymbol();
        activeType = loadInfo.getDataType();

        // Maybe this needs to return response?
        addDataSegment();

        setFileUrl(_params.get(0));

        // May need to make this return a response object at some point
        setActiveEntryList(loadInfo.getData());

        return new Response("Data was successfully loaded for " + getActiveSymbol(), true);

    }

    //=========================================================================
    // Random Facts API Connections
    //=========================================================================

    /**
     * Gets a quakka fact based on the settings map.
     */
    public static String getQuakkaFact() {
        String[] facts = settings.get("facts").split("\\|", 0);
        return facts[(int) (Math.random() * facts.length)];
    }

    /**
     * Checks that settings date is older than 24 hours and updates settings accordingly if so.
     */
    public static void loadQuokkaFacts() {

        String value = settings.get("lastQuokkaCall");

        LocalDateTime dateNow = LocalDateTime.now();

        if (value != null) {
            LocalDateTime dateSinceLastQuokkaCall = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            // 24 hours have passed. need new 5 strings and to update time
            if (dateNow.isAfter(dateSinceLastQuokkaCall)) {

                StringBuilder sb = new StringBuilder();

                ArrayList<String> facts = Qfacts.getList(5);

                //make call to quokkas api and save values
                for (int i = 0; i < facts.size(); i++) {
                    sb.append("|" + facts.get(i));
                }

                settings.put("facts", "Quokkas are really cute!" + sb.toString());
                settings.put("lastQuokkaCall", dateNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
            }

        } else {
            settings.put("facts", "Quokkas are really cute!");
            settings.put("lastQuokkaCall", dateNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
        }
    }

    //=========================================================================
    // Finance Data API Connections
    //=========================================================================

    /**
     * This function updates an entry in the map that already exists. if the
     * symbol doesnt exist in the map, then there is nothing to update and
     * the function exits.
     *
     * @param _arg1
     * @param _arg2
     * @return Response object with error or success state
     */
    public static Response update(int _arg1, int _arg2) {

        switch(activeType) {
            case 0: //stock
                return updateStock( _arg1, _arg2 );
            case 1: // crypto
                return updateCrypto( _arg1, _arg2 );
            default:
                return new Response("Cannot update, incorrect type...", false);
        }
    }

    /**
     * Update stock based off of call type and interval.
     *
     * @param _callType
     * @param _callInterval
     * @return
     */
    public static Response updateStock(int _callType, int _callInterval) {
        if (!stockData.containsKey(activeSymbol)) {
            return new Response( activeSymbol + " does not exist, update failed. ", false);
        }

        ArrayList<Entry> apiCallResult = Stocks.get(
                stockData.get(activeSymbol).getSymbol(),
                _callType,
                _callInterval
        );

        if(apiCallResult == null){
            return new Response("An error occurred while making update request. Update failed. ", false);
        }

        stockData.get(activeSymbol).setCallType(_callType);
        stockData.get(activeSymbol).setCallInterval(_callInterval);
        stockData.get(activeSymbol).setEntryList(apiCallResult);

        return new Response("Updated " + getActiveSymbol(), true);
    }

    /**
     * Update crypto based off of call type and market.
     *
     * @param _callType
     * @param _callMarket
     * @return
     */
    public static Response updateCrypto(int _callType, int _callMarket) {
        if (!cryptoData.containsKey(activeSymbol)) {
            return new Response( activeSymbol + " does not exist, update failed. ", false);
        }

        // This is done because at 0 (daily) it thinks it is intraday
        _callType++;
        ArrayList<Entry> api_call_result = Cryptos.get(
                cryptoData.get(activeSymbol).getSymbol(),
                getCryptoMarkets()[_callMarket],
                _callType
        );

        cryptoData.get(activeSymbol).setCallType(_callType);
        cryptoData.get(activeSymbol).setCallMarket(_callMarket);
        cryptoData.get(activeSymbol).setEntryList(api_call_result);

        if(cryptoData.get(activeSymbol).getEntryList().size() == 0) {
            return new Response("Error: An error occured while making update request. Update failed. ", false);
        } else {
            return new Response("Success: Updated " + getActiveSymbol(), true);
        }
    }

    /**
     * Update all iterates through each symbol in the finance data map and calls
     * update using the parameters for the call saved in the FinanceDataSegment.
     */
    public static Response updateAll() {

        Response updateAllResponse;

        updateAllResponse = updateAllStocks();

        if(!updateAllResponse.getSuccess()) {
            return updateAllResponse;
        }

        updateAllResponse = updateAllCryptos();

        if(!updateAllResponse.getSuccess()) {
            return updateAllResponse;
        }

        return new Response("All favorites were updated successfully", true);
    }

    /**
     * Update all stocks. Each stock is updated over a predefined interval, so this
     * can take some time. The interval keeps our program from making calls too fast
     * and erroring out.
     *
     * @return
     */
    private static Response updateAllStocks() {

        for(int i = 0; i < stockData.size(); i++) {

            // get next key value from stock data
            String nextKey = (String) stockData.keySet().toArray()[i];

            // set active symbol and type for update call which relies on this.
            setActiveSymbol(nextKey);
            setActiveType(0);

            Response updateResponse = updateStock(
                    stockData.get(nextKey).getCallType(),
                    stockData.get(nextKey).getCallInterval()
            );

            if(!updateResponse.getSuccess()) {
                return new Response ("An error occurred while updating " + getActiveSymbol() + ", update all failed", false);
            }

            try{
                TimeUnit.SECONDS.sleep(UPDATE_WAIT_SECONDS);
            } catch (InterruptedException e) {
                return new Response("An error occurred while waiting for next update. Update failed.", false);
            }
        }

        return new Response("All stocks updated", true);
    }

    /**
     * Update all cryptos. Each crypto is updated over a predefined interval, so this
     * can take some time. The interval keeps our program from making calls too fast
     * and erroring out.
     *
     * @return
     */
    private static Response updateAllCryptos() {
        for(int i = 0; i < cryptoData.size(); i++) {

            // get next key value from stock data
            String nextKey = (String) cryptoData.keySet().toArray()[i];

            // set active symbol and type for update call which relies on this.
            setActiveSymbol(nextKey);
            setActiveType(1);

            Response updateResponse = updateCrypto(
                    cryptoData.get(nextKey).getCallType(),
                    cryptoData.get(nextKey).getCallMarket()
            );

            if(!updateResponse.getSuccess()) {
                return new Response ("An error occurred while updating " + getActiveSymbol() + ", update all failed", false);
            }

            try{
                TimeUnit.SECONDS.sleep(UPDATE_WAIT_SECONDS);
            } catch (InterruptedException e) {
                return new Response("An error occurred while waiting for next update. Update failed.", false);
            }
        }

        return new Response("All stocks updated", true);
    }

    //=========================================================================
    // Cwacos Data Functions
    //=========================================================================

    /**
     * Add a favorite to the data state.
     *
     * @param _symbol
     * @param type
     * @return
     */
    public static Response addFavorite(String _symbol, int type) {

        //ATTN: maybe this switch could be more dynamic?
        switch(type){
            case 0: // stock type
                return addStock(_symbol);
            case 1: // crypto type
                return addCrypto(_symbol);
            default:
                return new Response("Favorite is of incorrect type...", false);
        }
    }

    /**
     * Adds a stock by first checking that a call can be made and is successful.
     * Stock is then added to map if it doesnt already exist.
     *
     * @param _symbol   stock/crypto symbol, i.e. "IBM"
     * @return If not null, an error message.
     */
    public static Response addStock(String _symbol) {

        if(stockData.containsKey(_symbol)) {
            return new Response("Stock symbol already exists...", false);
        }

        ArrayList<Entry> evaluator = Stocks.get(
                _symbol,
                1,
                0
        );

        if (evaluator == null) {
            return new Response("Stock symbol could not be validated...", false);
        }

        // The "ifAbsent" part here is redundant given we have already checked if it contains key
        stockData.putIfAbsent(
                _symbol,
                new StockDataSegment(
                        _symbol,
                        0,
                        3
                )
        );

        stockData.get(_symbol).setEntryList(new ArrayList<>());

        return new Response("Stock symbol " + _symbol + " was added.", true);
    }

    /**
     * Adds a crypto by first checking that a call can be made and is successful.
     * Crypto is then added to map if it doesnt already exist.
     *
     * @param _symbol
     * @return
     */
    public static Response addCrypto(String _symbol) {

        if (cryptoData.containsKey(_symbol)){
            return new Response("Crypto symbol already exists...", false);
        }

        ArrayList<Entry> evaluator = Cryptos.get(
                _symbol,
                "USD",
                1
        );

        if (evaluator == null){
            return new Response("Crypto symbol could not be validated...", false);
        }

        cryptoData.putIfAbsent(
                _symbol,
                new CryptoDataSegment(
                        _symbol,
                        1,
                        0
                )
        );

        cryptoData.get(_symbol).setEntryList(new ArrayList<>());

        return new Response("Crypto symbol " + _symbol + " added successfully." , true);
    }

    /**
     * Removes the DataSegment value associated with the _symbol
     * string key from the appropriate map.
     *
     * @param _symbol stock/crypto symbol, i.e. "IBM"
     * @return If not null, an error message.
     */
    public static Response removeFavorite(String _symbol, int type) {

        switch(type) {
            case 0: // remove stock
                return removeStock(_symbol);
            case 1: // remove crypto
                return removeCrypto(_symbol);
            default:
                return new Response("Favorite is of incorrect type...", false);
        }
    }

    /**
     * Remove stock from map based on symbol.
     *
     * @param _symbol
     * @return
     */
    public static Response removeStock(String _symbol) {
        if(!stockData.containsKey(_symbol)) {
            return new Response("Stock does not exist, cannot be removed...", false);
        }

        stockData.remove(_symbol);

        setNextActiveData();

        return new Response("Stock " + _symbol + " removed successfully.", true);
    }

    /**
     * Remove crypto from appropriate map based on symbol.
     *
     * @param _symbol
     * @return
     */
    public static Response removeCrypto(String _symbol) {
        if(!cryptoData.containsKey(_symbol)) {
            return new Response("Crypto does not exist, cannot be removed...", false);
        }

        cryptoData.remove(_symbol);

        setNextActiveData();

        return new Response("Crypto " + _symbol + " removed successfully.", true);
    }

    /**
     * Generate a default url to save to in the temp folder.
     *
     * @return
     */
    public static String generateDefaultUrl() {
        return "./" + TEMP_FOLDER + "/" + generateFileUrl();
    }

    /**
     * Generate the file url for saving on file close. the generated url is based off
     * of the data associated with the data being saved.
     *
     * @return
     */
    public static String generateFileUrl() {
        if (!dataAvailable()) {
            return null;
        }

        return getActiveSymbol() + "_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(getActiveEntryList().get(0).getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) + ".cw";
    }

    /**
     * Add a new datasegment to the appropriate data map based on type.
     */
    private static void addDataSegment() {
        switch(activeType) {
            case 0: //stock
                stockData.putIfAbsent(activeSymbol, new StockDataSegment(activeSymbol, activeType, 3));
                break;
            case 1: // crypto
                cryptoData.putIfAbsent(activeSymbol, new CryptoDataSegment(activeSymbol, activeType, 0));
                break;
        }
    }

    /**
     * Resets activeData and activeType to the first element of the
     * data maps assuming they are not empty. The order of handling is
     * as follows:
     *
     * stockData -> cryptoData
     *
     * Meaning, if an element in stockData exists, it will be set, else,
     * if an element exists in cryptoData it will then be set.
     */
    public static void setNextActiveData() {

        if(stockData.keySet().size() != 0) {
            activeSymbol = (String) stockData.keySet().toArray()[0];
            activeType = 0;
        } else if (cryptoData.keySet().size() != 0) {
            activeSymbol = (String) cryptoData.keySet().toArray()[0];
            activeType = 1;
        } else {
            activeSymbol = null;
        }
    }

    //=========================================================================
    // Data State
    //=========================================================================

    /**
     * Query if data is available. i.e. is there data for the current financial
     * symbol being viewed.
     *
     * @return boolean representing if data exists
     */
    public static boolean dataAvailable() {
        return !((getActiveEntryList() == null) || (getActiveEntryList().size() == 0));
    }

    //=========================================================================
    // GETTERS
    //=========================================================================

    /**
     * Return active symbol.
     * @return
     */
    public static String getActiveSymbol() {
        return activeSymbol;
    }

    /**
     * Return active data type.
     * @return
     */
    public static int getActiveType() {
        return activeType;
    }

    /**
     * Get call types from stocks or cryptos.
     * @return
     */
    public static String[] getCallTypes() {
        switch(activeType) {
            case 0:
                return Stocks.getCallTypes();
            case 1:
                return Cryptos.getCallTypes();
            default:
                return null;
        }
    }

    /**
     * Get stock intervals.
     * @return
     */
    public static String[] getStockIntervals() {
        switch(activeType) {
            case 0:
                return Stocks.getCallIntervals();
            default:
                return null;
        }
    }

    /**
     * Get crypto markets.
     * @return
     */
    public static String[] getCryptoMarkets() {
        switch(activeType) {
            case 1:
                return Cryptos.getCallMarkets();
            default:
                return null;
        }
    }

    /**
     * Gets ArrayList of entries for data being actively viewed.
     * @return
     */
    public static ArrayList<Entry> getActiveEntryList() {
        try {
            switch (activeType) {
                case 0:
                    return stockData.get(activeSymbol).getEntryList();
                case 1:
                    return cryptoData.get(activeSymbol).getEntryList();
                default:
                    return null;
            }
            // the purpose of this catch is mainly for when there are no active element
            // i.e. the activeSymbol is null. It is easier to do this than the other
            // way I could have handled it.
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets file url for data being viewed.
     * @return
     */
    public static String getFileUrl() {
        switch(activeType) {
            case 0:
                return stockData.get(activeSymbol).getFileUrl();
            case 1:
                return cryptoData.get(activeSymbol).getFileUrl();
            default:
                return null;
        }
    }

    /**
     * Gets the length of how long the user will wait as a string
     * based on the size of the data that needs to be updated.
     * @return
     */
    public static String getUpdateWaitTimeAsString() {

        long duration = getDataSize() * UPDATE_WAIT_SECONDS;

        long days = TimeUnit.SECONDS.toDays(duration);
        duration -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = TimeUnit.SECONDS.toSeconds(duration);

        StringBuilder msg = new StringBuilder( );
        if (days!=0) {
            msg.append( days+" day(s)");
        }
        if (hours!=0) {
            msg.append( hours+" hours(s)");
        }
        if (minutes!=0) {
            msg.append( minutes+" minutes(s)");
        }
        if (seconds!=0) {
            msg.append( seconds+" seconds(s)");
        }
        return msg.toString();
    }

    /**
     * Gets the size of the two data stores for stocks and cryptos.
     *
     * If more types get added in the future, this may not be the best way to do it.
     *
     * @return Number of all stock and crypto symbols
     */
    private static int getDataSize() {
        return stockData.size() + cryptoData.size();
    }

    /**
     * Get the max profit analysis as an array of strings for the
     * currently viewed object.
     * @return
     */
    public static String[] getMaxProfit() {
        return MaxProfit.getMaxProfit(getActiveEntryList());
    }

    /**
     * Gets the list of stock symbols that make up the keys to the
     * stocks map.
     * @return
     */
    public static ArrayList<String> getStockSymbols() {

        ArrayList<String> symbols = new ArrayList<String>();

        for (Map.Entry<String, StockDataSegment> entry : stockData.entrySet()) {
            symbols.add(entry.getKey());
        }

        return symbols;
    }

    /**
     * Gets the list of crypto symbols that make up the keys to the
     * cryptos map.
     * @return
     */
    public static ArrayList<String> getCryptoSymbols() {

        ArrayList<String> symbols = new ArrayList<String>();

        for (Map.Entry<String, CryptoDataSegment> entry : cryptoData.entrySet()) {
            symbols.add(entry.getKey());
        }

        return symbols;
    }

    //=========================================================================
    // SETTERS
    //=========================================================================

    /**
     * Set active symbol.
     * @param _symbol
     */
    public static void setActiveSymbol(String _symbol) {
        activeSymbol = _symbol;
    }

    /**
     * Set active type.
     * @param _type
     */
    public static void setActiveType(int _type) {
        activeType = _type;
    }

    /**
     * Sets active entry list for data being viewed.
     * @param _data
     */
    private static void setActiveEntryList(ArrayList<Entry> _data) {
        switch(activeType) {
            case 0:
                stockData.get(activeSymbol).setEntryList(_data);
                break;
            case 1:
                cryptoData.get(activeSymbol).setEntryList(_data);
                break;
        }
    }

    /**
     * Sets file url for data being viewed.
     * @param _url
     */
    public static void setFileUrl(String _url) {
        switch(activeType) {
            case 0:
                stockData.get(activeSymbol).setFileUrl(_url);
                break;
            case 1:
                cryptoData.get(activeSymbol).setFileUrl(_url);
                break;
        }
    }
}