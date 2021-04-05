import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

public class AlphaVantageAPITranslator extends AlphaAPIDataGet{


    /**
     * Creates connection to API, stores JSON, parses JSON, and returns data organized in an ArrayList of Entry Objects.
     .    * Limited to 5 calls per minute and 500 calls per day.
     * @param _stock String with desired stock symbol. (Example: "IBM")
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @param _interval String with desired time interval, for Intraday only (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     *                  Use NO_INTERVAL for any call that is not intraday.
     * @return ArrayList of Entry objects.
     */
    public static ArrayList<Entry> getStockInfo(String _stock, int _callType, int _interval){
        try {
            // Create the API url.
            URL url = getStockURL(_stock, _interval, _callType);

            // Save the main JSON file.
            JSONObject JSONFile = getJSON(url);

            //Translate the interval used in URL to correct format needed to parse the JSON file.
            String JSONInterval;
            if(_interval == NO_INTERVAL)
                JSONInterval = translateStockJSONInterval(_callType);
            else
                JSONInterval = translateStockJSONInterval(_interval);

            //If there is an error receiving the JSONObject OR the time series interval then the function will exit.
            if(JSONFile == null || JSONInterval == null)
                return null;

            //Create JSON object that will hold only the information for the given time interval.
            JSONObject timeSeries = JSONFile.getJSONObject(JSONInterval);

            //Parse the given interval JSON and return the data in an ArrayList of Entry objects.
            return parseStockJSON(timeSeries, _callType);
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex);
            return null;
        }
    }

    /** Creates connection to API, stores JSON, parses JSON, and returns intraday data organized in an ArrayList of Entry Objects.
     .    * Limited to 5 calls per minute and 500 calls per day.
     *
     * @param _crypto String with desired crypto symbol. (Example: "BTC")
     * @param _market String with the desired market/currency to return data in. (Example: "USD")
     * @param _callType String used to select the type of API call to be made, intraday cannot be used with this call. (Options: "daily", "weekly", "monthly")
     * @return ArrayList of Entry objects.
     */
    public static ArrayList<Entry> getCryptoInfo(String _crypto, String _market, int _callType) {
        try {
            // Create the API url
            URL url = getCryptoURL(_crypto, _market, _callType);

            // Save the main JSON file
            JSONObject JSONFile = getJSON(url);

            //Translate the callType used in URL to correct format needed to parse the JSON file.
            String JSONInterval = translateCryptoJSONInterval(_callType);

            //If there is an error receiving the JSONObject OR the time series interval then the function will exit.
            if (JSONFile == null || JSONInterval == null)
                return null;

            //Create JSON object that will hold only the information for the given time interval.
            JSONObject timeSeries = JSONFile.getJSONObject(JSONInterval);

            return parseCryptoJSON(timeSeries, _market);
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex);
            return null;
        }
   }







}
