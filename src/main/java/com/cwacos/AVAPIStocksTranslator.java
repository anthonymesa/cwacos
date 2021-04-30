package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: 
 * 
 * Contributing Authors:
 *      Michael Leonard
 *      Anthony Mesa
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AVAPIStocksTranslator extends AlphaVantageConnection implements StocksAdapter{

    /**
     * Creates connection to API, stores JSON, parses JSON, and returns data organized in an ArrayList of Entry Objects.
     .    * Limited to 5 calls per minute and 500 calls per day.
     * @param _stock String with desired stock symbol. (Example: "IBM")
     * @param _callType Int from 0 to 3 where,
     *                      0 = Intraday, 1 = Daily, 2 = Weekly, 3 = Monthly
     * @param _interval Int from 0 to 5 where,
     *                      0 = No interval, 1 = One Minute, 2 = Five Minutes, 3 = Fifteen Minutes, 4 = Thirty Minutes, 5 = One Hour
     * @return ArrayList of Entry objects.
     */
    public ArrayList<Entry> getStocksData(String _stock, int _callType, int _interval){
        try {
            //Translate the given call and interval into Enums
            CallType callEnum = translateTypeEnum(_callType);
            CallInterval intervalEnum = translateIntervalEnum(_interval);

            // Create the API url.
            URL url = getStockURL(_stock, intervalEnum, callEnum);

            //System.out.println(url.toString());

            // Save the main JSON file.
            JSONObject JSONFile = getJSON(url);

            //Translate the interval used in URL to correct format needed to parse the JSON file.
            String JSONInterval;
            if(intervalEnum == CallInterval.NO_INTERVAL)
                JSONInterval = translateStockJSONInterval(callEnum);
            else
                JSONInterval = translateStockJSONInterval(intervalEnum);

            //If there is an error receiving the JSONObject OR the time series interval then the function will exit.
            if(JSONFile == null || JSONInterval == null)
                return null;

            //Create JSON object that will hold only the information for the given time interval.
            JSONObject timeSeries = JSONFile.getJSONObject(JSONInterval);

            //Parse the given interval JSON and return the data in an ArrayList of Entry objects.
            return parseStockJSON(timeSeries, callEnum);
        }
        catch (Exception ex) {
            //System.out.println("Error: " + ex);
            return null;
        }
    }

    /**
     * Used to obtain valid API url for any daily, weekly, monthly, or intraday call with the given stock and interval.
     * @param _stock String with desired stock symbol. (Example: "IBM")
     * @param _interval String with desired time interval for Intraday (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     *                  Send in _interval as null if using anything except the intraday call.
     * @param _callType String used to select the type of API call to be made. (Options: "daily", "weekly", "monthly", "intraday")
     * @return A URL object ready to make an API call.
     * @throws MalformedURLException
     */
    private static URL getStockURL(String _stock, CallInterval _interval, CallType _callType) throws MalformedURLException {
        String function;
        String interval = "";

        //Translate the given interval to a string for the URL if the intraday call is being used
        if(_callType == CallType.INTRADAY_CALL)
            interval += "&interval=" + translateIntradayInterval(_interval);

        //API function being called
        function = translateStockCallType(_callType);

        return new URL(baseURLAlpha + "function=" + function + "&symbol=" + _stock + interval + "&apikey=" + apiKeyAlpha);

    }

    /**
     * Takes an intraday, daily, weekly, or monthly stock JSON and parses the data into entry objects. These objects are stored in an ArrayList and returned.
     * @param timeSeries JSONObject containing stock info for a specific type of API call.
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return ArrayList of stock Entries.
     */
    private static ArrayList<Entry> parseStockJSON(JSONObject timeSeries, CallType _callType) throws JSONException, ParseException {
        //Create iterator to parse JSONObject
        Iterator<String> keys = timeSeries.keys();

        //Dynamic Arrays to hold values from JSON
        ArrayList<Entry> stockInfo = new ArrayList<>();

        String open, high, low, close, volume;
        DateFormat format;

        //Use Special format for Intraday call, otherwise use the default format.
        if(_callType == CallType.INTRADAY_CALL)
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        else
            format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);


        //Iterate the JSON and store all appropriate values in correct ArrayLists
        while(keys.hasNext()) {

            String key = keys.next();
            if (timeSeries.get(key) instanceof JSONObject) {
                open = ((JSONObject) timeSeries.get(key)).getString("1. open");
                high = ((JSONObject) timeSeries.get(key)).getString("2. high");
                low = ((JSONObject) timeSeries.get(key)).getString("3. low");
                close = ((JSONObject) timeSeries.get(key)).getString("4. close");
                volume = ((JSONObject) timeSeries.get(key)).getString("5. volume");
                Entry currentEntry = new Entry( Double.parseDouble(open),
                        Double.parseDouble(close),
                        Double.parseDouble(low),
                        Double.parseDouble(high),
                        Integer.parseInt(volume),
                        format.parse(key) );
                stockInfo.add(currentEntry);
            }
        }
        Collections.sort(stockInfo);
        return stockInfo;
    }

    /**
     * This method is used to transform the int call type Enum from the AlphpaAPIDataGet, into a string to be used in parsing the stock JSON.
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return String usable for parsing fields in a stock JSON.
     */
    private static String translateStockCallType(CallType _callType){
        switch (_callType) {
            case INTRADAY_CALL:
                return "TIME_SERIES_INTRADAY";

            case DAILY_CALL:
                return "TIME_SERIES_DAILY";

            case WEEKLY_CALL:
                return "TIME_SERIES_WEEKLY";

            case MONTHLY_CALL:
                return "TIME_SERIES_MONTHLY";

            default:
                //Returns NULL for error checking and reports error.
                //System.out.println("Not a valid Call Type.");
                return null;
        }
    }

    /**
     * The time interval String used in the URL is different than the time interval String used to parse the JSON.
     * This method will take the interval used in the URL and return a correctly formatted interval String for parsing.
     * @param _rawInterval Time interval String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    private static String translateStockJSONInterval(CallInterval _rawInterval){

        //All intervals start with this as a base string to be appended to.
        String base = "Time Series ";

        switch(_rawInterval) {
            case MINUTE_1:
                return base + "(1min)";

            case MINUTE_5:
                return base + "(5min)";

            case MINUTE_15:
                return base + "(15min)";

            case MINUTE_30:
                return base + "(30min)";

            case MINUTE_60:
                return base + "(60min)";

            default:
                //Returns NULL for error checking and reports error.
               //System.out.println("Not a valid Interval.");
                return null;
        }
    }

    /**
     * The time interval String used in the URL is different than the time interval String used to parse the JSON.
     * This method will take the interval used in the URL and return a correctly formatted interval String for parsing.
     * @param _rawInterval Time interval String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    private static String translateStockJSONInterval(CallType _rawInterval){

        //All intervals start with this as a base string to be appended to.
        String base = "Time Series ";

        switch(_rawInterval) {
            case DAILY_CALL:
                return base + "(Daily)";

            case WEEKLY_CALL:
                return "Weekly Time Series";

            case MONTHLY_CALL:
                return "Monthly Time Series";

            default:
                //Returns NULL for error checking and reports error.
                //System.out.println("Not a valid Interval.");
                return null;
        }
    }

    public String[] getCallTypes() {
        return new String[] { "Intraday", "Daily", "Weekly", "Monthly" };
    }

    public String[] getCallIntervals() {
        return new String[] { "None", "1 min.", "5 min.", "15 min.", "30 min.", "60 min." };
    }
}
