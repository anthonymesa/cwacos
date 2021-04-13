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

    public static Map<String, String> settings = new HashMap<String, String>();
    private static String file_url = "./res/cwacossettings.conf";

    // This map represents a mapped list of favorite data
    // Map of strings to Ticker objects
    // e.g. "GME" --> new Ticker(s, t, d)
    private static Map<String, FinanceDataSegment> financeData = new HashMap<String, FinanceDataSegment>();

    //======================= STATE ===========================

    /**
     * Load the state of the software from local files and initialize data sources
     */
    public static void loadState() {
        loadSettings();
        loadQuakkaFacts();
        initDataSources();

        // parse through the symbols value and fileurls values in the settings, add them to favorites, and then
        // load their data using their respective urls

        // for each symbol in settings, add to favorites and populate data with data at file url
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
    private static void initDataSources(){
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
            saveData(entry.getKey());
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
     * @param _symbol stock/crypto symbol, i.e. "IBM"
     */
    public static String saveData(String _symbol) {

        // check if finance data contains the symbol as key
        if (!financeData.containsKey(_symbol)) {
            return "Symbol is not a favorite, no file was saved.";
        }

        if (financeData.get(_symbol).data == null) {
            System.out.println("There is no data to save.");
            return "There is no data to save.";
        }

        // generate filename and save to local folder
        String file_url = "./res/" + _symbol + "_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(financeData.get(_symbol).data.get(0).dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        financeData.get(_symbol).url = file_url;

        ArrayList<String> save_parameters = new ArrayList<String>(
                Arrays.asList(
                        file_url
                )
        );

        save_parameters.add(((Integer) financeData.get(_symbol).call_type).toString());

        ArrayList<Entry> data_to_save = financeData.get(_symbol).data;
        DataStorage.save(save_parameters, data_to_save);

        return null;
    }

    /**
     * Load the file to populate the data for the current symbol in the finance data map
     * <p>
     * Data can only be loaded for stocks/cryptos that have already been added to the finance data map.
     *
     * @param _symbol stock/crypto symbol, i.e. "IBM"
     * @param _params an array list containing the parameters
     *                for the current method of saving data.
     *                <p>
     *                current method is local file storage so _params should
     *                only contain a file url
     */
    public static ArrayList<Entry> loadData(String _symbol, ArrayList<String> _params) {

        // check if finance data contains the symbol as key
        if (!financeData.containsKey(_symbol)) {
            return null;
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
        financeData.get(_symbol).data = loaded_data;

        return financeData.get(_symbol).data;
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
     * @param _symbol        stock/crypto symbol, i.e. "IBM"
     * @param _call_type     integer that matches available call types in AlphaAPIDataGet class
     * @param _call_interval integer that matches available call intervals in AlphaAPIDataGet class
     */

    // update crypto vs update stock

    public static ArrayList<Entry> update(String _symbol, int _call_type, int _call_interval) {

        if (!financeData.containsKey(_symbol)) {
            return null;
        }

        // make a call to the api using the data values associated with the symbol being updated.

        ArrayList<Entry> api_call_result = Stocks.get(
                financeData.get(_symbol).symbol,
                _call_type,
                _call_interval
        );

        financeData.get(_symbol).call_type = _call_type;
        financeData.get(_symbol).call_interval = _call_interval;
        financeData.get(_symbol).data = api_call_result;

        return financeData.get(_symbol).data;
    }

    /**
     * Update all iterates through each symbol in the finance data map and calls
     * update using the parameters for the call saved in the FinanceDataSegment
     */
    public static void updateAll() {
        for (Map.Entry<String, FinanceDataSegment> each : financeData.entrySet()) {
            update(
                    each.getValue().symbol,
                    each.getValue().call_type,
                    each.getValue().call_interval
            );
        }
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

        // here we are making a call to the api to see if it returns a null array
        // or not, which will tell us if the ticker exists or not.
        ArrayList<Entry> evaluator = Stocks.get(
                _symbol,
                2,
                10
        );

        // if the call returned a null array, then the favorite cant be added.
        if (evaluator != null) {
            financeData.putIfAbsent(
                    _symbol,
                    new FinanceDataSegment(
                            _symbol,
                            _dataType,
                            1,
                            15
                    )
            );
            return null;
        } else {
            return "EROR: CwacosData.AddFavorite - New favorite failed validation";
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
        financeData.remove(_symbol);

        if (!financeData.containsKey(_symbol)) {
            return "Symbol " + _symbol + " does not exist.";
        }

        return null;
    }
}
