import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class AlphaVantageAPITranslator extends AlphaAPIDataGet{


    /**
     * Creates connection to API, stores JSON, parses JSON, and returns intraday data organized in an ArrayList of Entry Objects.
     .    * Limited to 5 calls per minute and 500 calls per day.
     * @param _stock String with desired stock symbol. (Example: "IBM")
     * @param _interval String with desired time interval for Intraday (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     * @return ArrayList of Entry objects.
     */
    public static ArrayList<Entry> getStockInfo(String _stock, int _interval, int _callType){
        try {
            // Create the API url.
            URL url = getStockURL(_stock, _interval, _callType);

            // Save the main JSON file.
            JSONObject JSONFile = getStockJSON(url);

            //Translate the interval used in URL to correct format needed to parse the JSON file.
            String JSONInterval;
            if(_interval == NO_INTERVAL)
                JSONInterval = translateInterval(_callType);
            else
                JSONInterval = translateInterval(_interval);

            //If there is an error receiving the JSONObject OR the time series interval then the function will exit.
            if(JSONFile == null || JSONInterval == null){
                return null;
            }

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

   public static ArrayList<Entry> getCryptoInfo(){
        return null;
   }







}
