import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CwacosData {

    //===================== DATA STORAGE ======================

    public static DataStorage storage = new DataStorage(DataStorage.StorageType.LOCAL);

    /*
     * Save the stock symbol's associated entry data to a file by
     * looking up that stock symbol in the finance data map and
     * saving its data attribute of ArrayList<Entry> type.
     *
     * @params _symbol - stock/crypto symbol, i.e. "IBM"
     * @params _params
     *      an array list containing the parameters
     *      for the current method of saving data.
     *
     *      current method is local file storage so _params should
     *      only contain a file url
     */
    public static String save(String _symbol){

        // check if finance data contains the symbol as key
        if(!finance_data.containsKey(_symbol)){
            return "Symbol is not a favorite, no file was saved.";
        }

        if(finance_data.get(_symbol).data == null){
            return "There is no data to save.";
        }

        // generate filename and save to local folder
        String file_url = "./res/" + _symbol + "_" + finance_data.get(_symbol).data.get(0).close;

        ArrayList<String> save_parameters = new ArrayList<String>(
            Arrays.asList(
                file_url
            )
        );

        ArrayList<Entry> data_to_save = finance_data.get(_symbol).data;
        storage.save(save_parameters, data_to_save);

        return null;
    }

    /*
     * Load the file to populate the data for the current symbol in the finance data map
     *
     * Data can only be loaded for stocks/cryptos that have already been added to the finance data map.
     *
     * @params _symbol - stock/crypto symbol, i.e. "IBM"
     * @params _params -
     *      an array list containing the parameters
     *      for the current method of saving data.
     *
     *      current method is local file storage so _params should
     *      only contain a file url
     */
    public static ArrayList<Entry> load(String _symbol, ArrayList<String> _params){

        // check if finance data contains the symbol as key
        if(!finance_data.containsKey(_symbol)){
            return null;
        }

        ArrayList<Object> data = storage.load(_params);

        /* This is for casting the objects returned into
         * entry objects. If we ever change Entry or use a
         * different type in the future, this is the only
         * thing we need to change.
         */
        ArrayList<Entry> loaded_data = new ArrayList<>();
        for(Object each : data){
            loaded_data.add((Entry) each);
        }

        // save the loaded data to the finance data value with symbol key
        finance_data.get(_symbol).data = loaded_data;

        return finance_data.get(_symbol).data;
    }

    //=================== API INTERACTION =====================

    /*
     * this function updates an entry in the map that already exists. if the
     * symbol doesnt exist in the map, then there is nothing to update and
     * the function exits.
     *
     * @params _symbol - stock/crypto symbol, i.e. "IBM"
     * @params _call_type - integer that matches available call types in AlphaAPIDataGet class
     * @param _call_interval - integer that matches available call intervals in AlphaAPIDataGet class
     */
    public static ArrayList<Entry> update(String _symbol, int _call_type, int _call_interval){

        if(!finance_data.containsKey(_symbol)){
            return null;
        }

        // make a call to the api using the data values associated with the symbol being updated.

        ArrayList<Entry> api_call_result = AlphaVantageAPITranslator.getStockInfo(
                finance_data.get(_symbol).symbol,
                _call_interval,
                _call_type
        );

        finance_data.get(_symbol).call_type = _call_type;
        finance_data.get(_symbol).call_interval = _call_interval;
        finance_data.get(_symbol).data = api_call_result;

        return finance_data.get(_symbol).data;
    }

    /*
     * Update all iterates through each symbol in the finance data map and calls
     * update using the parameters for the call saved in the FinanceDataSegment
     */
    public static void updateAll(){
        for(Map.Entry<String, FinanceDataSegment> each : finance_data.entrySet()){
            update(
              each.getValue().symbol,
              each.getValue().call_type,
              each.getValue().call_interval
            );
        }
    }

    //===================== PROGRAM DATA ======================

    // This map represents a mapped list of favorite data
    // Map of strings to Ticker objects
    // e.g. "GME" --> new Ticker(s, t, d)
    private static Map<String, FinanceDataSegment> finance_data = new HashMap<String, FinanceDataSegment>();

    /*
     * when a user clicks add favorite, a dialogue window should pop up
     * that lets them put in a stock ticker and choose whether it is
     * stock or it is crypto, then this function should be run on success.
     *
     * Could be using exceptions or something to translate errors, but honestly,
     * these string returns will do just fine.
     *
     * All financial data segments are initialized with call_type intraday,
     * with an interval of 30 minutes.
     *
     * @params _symbol - stock/crypto symbol, i.e. "IBM"
     * @param _data_type - integer for datatype. 0 = null, 1 = stock, 2 = crypto
     */
    public static String AddFavorite(String _symbol, int _data_type){

        // here we are making a call to the api to see if it returns a null array
        // or not, which will tell us if the ticker exists or not.
        ArrayList<Entry> evaluator = AlphaVantageAPITranslator.getStockInfo(
                _symbol,
                10,
                4
        );

        // if the call returned a null array, then the favorite cant be added.
        if(evaluator != null){
            finance_data.putIfAbsent(
                _symbol,
                new FinanceDataSegment(
                    _symbol,
                    _data_type,
                    1,
                    15
                )
            );
            return null;
        } else {
            return "EROR: CwacosData.AddFavorite - New favorite failed validation";
        }
    }

    /*
     * @params _symbol - stock/crypto symbol, i.e. "IBM"
     */
    public static String RemoveFavorite(String _symbol){
        finance_data.remove(_symbol);

        if(!finance_data.containsKey(_symbol)){
            return "Symbol " + _symbol + " does not exist.";
        }

        return null;
    }
}
