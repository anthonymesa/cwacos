/*
Last updated:
Purpose of this class:
Contributing Authors:
 */
/**
 * This is necessary
 */

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CwacosData {

    private static Map<String, String> settings = new HashMap<String, String>();
    private static String file_url = "./res/cwacossettings.conf";
    private static String activeData = "";

    // This map represents a mapped list of favorite data
    // Map of strings to Ticker objects
    // e.g. "GME" --> new Ticker(s, t, d)
    private static Map<String, FinanceDataSegment> financeData = new HashMap<String, FinanceDataSegment>();

    //======================= STATE ===========================

    /**
     * Load the state of the software from local files and initialize data sources
     */
    public static void loadState() {
        CwacosData.initDataSources();
        loadSettings();
        loadQuakkaFacts();

        if((settings.get("favorites") != null) && (settings.get("fileLocations") != null)) {

            String[] symbols = settings.get("favorites").split("\\|", 0);
            String[] fileLocations = settings.get("fileLocations").split("\\|", 0);

            if ((symbols.length == fileLocations.length) && (symbols.length != 0)) {
                for (int i = 0; i < symbols.length; i++) {
                    //ATTN: datatype needs to be known at this point
                    financeData.put(symbols[i], new FinanceDataSegment(symbols[i], 0, 1, 0));

                    ArrayList<String> load_parameters = new ArrayList<String>(
                            Arrays.asList(
                                    fileLocations[i]
                            )
                    );

                    //ATTN: possible loading issues need to be caught here
                    CwacosData.loadData(load_parameters);
                }
            }
        }
    }

    /**
     * Loads the settings from local files
     */
    public static void loadSettings() {

        ArrayList<String> load_parameters = new ArrayList<String>(
                Arrays.asList(
                        file_url
                )
        );

        settings = DataStorage.loadSettings(load_parameters);
    }

    /**
     * Initializes static datastorage and datasources
     */
    public static void initDataSources(){
        DataStorage.init();
        Stocks.init();
        Cryptos.init();
        Qfacts.init();
    }

    /**
     * Saves the information state of the application. Supposed to take
     * place on close, should be one of the last things to do.
     */
    public static void saveState() {

        StringBuilder list_symbols = new StringBuilder();
        StringBuilder list_file_urls = new StringBuilder();

        // these are kept seperate because you cant concurrently modify
        for (Map.Entry<String, FinanceDataSegment> entry : financeData.entrySet()) {
            activeData = entry.getKey();
            saveData();
        }

        // gather state info to save for each element in the financeData
        for (Map.Entry<String, FinanceDataSegment> entry : financeData.entrySet()) {
            list_symbols.append(entry.getKey() + "|");
            list_file_urls.append(entry.getValue().url + "|");
        }

        settings.put("favorites", list_symbols.toString());
        settings.put("fileLocations", list_file_urls.toString());

        saveSettings();
    }

    public static void saveSettings() {
        String file_url = "./res/cwacossettings.conf";

        ArrayList<String> saveParameters = new ArrayList<String>(
                Arrays.asList(
                        file_url
                )
        );

        DataStorage.saveSettings(settings, saveParameters);
    }

    //===================== DATA STORAGE ======================

    /**
     * Save the stock symbol's associated entry data to a file by
     * looking up that stock symbol in the finance data map and
     * saving its data attribute of ArrayList<Entry> type.
     *
     */
    public static String saveData() {

        // check if finance data contains the symbol as key
        if (!financeData.containsKey(activeData)) {
            return "Error: Symbol " + activeData +" is not a favorite, no file was saved.";
        }

        if (financeData.get(activeData).data == null) {
            return "Error: There is no data to save.";
        }

        // generate filename and save to local folder
        String file_url = "./../" + activeData + "_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(financeData.get(activeData).data.get(0).getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        // save local file url to map
        financeData.get(activeData).url = file_url;

        // create list of save paramaters to send to send funcition
        ArrayList<String> save_parameters = new ArrayList<String>(
                Arrays.asList(
                        file_url
                )
        );

        save_parameters.add(((Integer) financeData.get(activeData).call_type).toString());

        ArrayList<Entry> data_to_save = financeData.get(activeData).data;
        DataStorage.save(save_parameters, data_to_save);

        return "Data saved successfully at: " + file_url;
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
    public static String loadData(ArrayList<String> _params) {

        // check if finance data contains the symbol as key
        if (!financeData.containsKey(activeData)) {
            return "Error: Can not load file data, symbol does not exist in favorites";
        }

        ArrayList<Object> data = DataStorage.load(_params);

        /* This is for casting the objects returned into
         * entry objects. If we ever change Entry or use a
         * different type in the future, this is the only
         * thing we need to change.
         */
        ArrayList<Entry> loaded_data = new ArrayList<>();
        for (Object each : data) {
            loaded_data.add((Entry) each);
        }

        // save the loaded data to the finance data value with symbol key
        financeData.get(activeData).data = loaded_data;

        if (financeData.get(activeData).data != null) {
            return "Success: data was loaded for " + activeData;
        } else {
            return "Error: an error occured on import";
        }
    }

    //=================== API INTERACTION =====================

    public static String getQuakkaFact() {
        String[] facts = settings.get("facts").split("\\|", 0);
        return facts[(int) (Math.random() * facts.length)];
    }

    /**
     * Checks that settings date is older than 24 hours and updates settings accordingly if so.
     */
    public static void loadQuakkaFacts() {

        String value = settings.get("lastQuakkaCall");

        LocalDateTime dateNow = LocalDateTime.now();

        if (value != null) {
            LocalDateTime dateSinceLastQuakkaCall = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            // 24 hours have passed. need new 5 strings and to update time
            if (dateNow.isAfter(dateSinceLastQuakkaCall)) {

                StringBuilder sb = new StringBuilder();

                //make call to quakkas api and save values
                for (int i = 0; i < 5; i++) {
                    //sb.append("|" + RandomFactsAPITranslator.getQuokkasFact());
                }

                settings.put("facts", "Quakkas are really cute!" + sb.toString());
                settings.put("lastQuakkaCall", dateNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
            }

        } else {
            settings.put("facts", "Quakkas are really cute!");
            settings.put("lastQuakkaCall", dateNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
        }
    }

    /**
     * this function updates an entry in the map that already exists. if the
     * symbol doesnt exist in the map, then there is nothing to update and
     * the function exits.
     *
     * @param _call_type     integer that matches available call types in AlphaAPIDataGet class
     * @param _call_interval integer that matches available call intervals in AlphaAPIDataGet class
     */

    // update crypto vs update stock

    public static String update(int _call_type, int _call_interval) {

        if (!financeData.containsKey(activeData)) {
            return "Error: " + activeData + " does not exist, update failed. ";
        }

        // make a call to the api using the data values associated with the symbol being updated.

        ArrayList<Entry> api_call_result = Stocks.get(
                financeData.get(activeData).symbol,
                _call_type,
                _call_interval
        );

        financeData.get(activeData).call_type = _call_type;
        financeData.get(activeData).call_interval = _call_interval;
        financeData.get(activeData).data = api_call_result;

        if(api_call_result == null){
            return "Error: An error occured while making update request. Update failed. ";
        }

        return null;
    }

    /**
     * Update all iterates through each symbol in the finance data map and calls
     * update using the parameters for the call saved in the FinanceDataSegment
     */
    public static String updateAll() {
        for (Map.Entry<String, FinanceDataSegment> each : financeData.entrySet()) {
            String updateStatus = update(
                    each.getValue().call_type,
                    each.getValue().call_interval
            );

            if(updateStatus != null){
                return updateStatus + each + " could not be updated. 'Update All' failed. Only some symbols may have been updated";
            }
        }

        return "Success: All symbols were updated.";
    }

    //===================== PROGRAM DATA ======================

    /**
     * when a user clicks add favorite, a dialogue window should pop up
     * that lets them put in a stock ticker and choose whether it is
     * stock or it is crypto, then this function should be run on success.
     * <p>
     * Could be using exceptions or something to translate errors, but honestly,
     * these string returns will do just fine.
     * <p>
     * All financial data segments are initialized with call_type intraday,
     * with an interval of 30 minutes.
     *
     * @param _symbol   stock/crypto symbol, i.e. "IBM"
     * @param _dataType integer for datatype. 0 = null, 1 = stock, 2 = crypto
     * @return If not null, an error message.
     */
    public static String addFavorite(String _symbol, int _dataType) {

        if(financeData.containsKey(_symbol)) {
            return "Error: Symbol already exists.";
        }

        // here we are making a call to the api to see if it returns a null array
        // or not, which will tell us if the ticker exists or not.
        ArrayList<Entry> evaluator = Stocks.get(
                _symbol,
                1,
                0
        );

        // if the call returned a null array, then the favorite cant be added.
        if (evaluator != null) {
            financeData.putIfAbsent(
                    _symbol,
                    new FinanceDataSegment(
                            _symbol,
                            _dataType,
                            1,
                            0
                    )
            );
            return "Success: Favorite " + _symbol + " added";
        } else {
            return "ERROR: CwacosData.AddFavorite - New favorite failed validation";
        }
    }

    /**
     * Removes the FinanceDataSegment value associated with the _symbol
     * string key from the financeData map.
     *
     * @param _symbol stock/crypto symbol, i.e. "IBM"
     * @return If not null, an error message.
     */
    public static String removeFavorites(String _symbol) {
        if (!financeData.containsKey(_symbol)) {
            return "Symbol " + _symbol + " does not exist.";
        }

        financeData.remove(_symbol);

        return "Success: Favorite " + _symbol + " removed";
    }

    public static String getActiveData() {
        return activeData;
    }
    
    public static void setActiveData(String _symbol) {
        activeData = _symbol;
    }

    public static boolean existData(String _symbol) {
        return financeData.containsKey(_symbol);
    }

    public static ArrayList<Entry> getActiveEntryList() {
        return financeData.get(activeData).data;
    }
}
