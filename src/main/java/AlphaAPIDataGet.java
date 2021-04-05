import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AlphaAPIDataGet {
    protected static final String baseURLAlpha = "https://www.alphavantage.co/query?";
    protected static final String apiKeyAlpha = "MO9QU9JAPBBPX5T7";

    public static final int INTRADAY_CALL = 1;
    public static final int DAILY_CALL = 2;
    public static final int WEEKLY_CALL = 3;
    public static final int MONTHLY_CALL = 4;

    public static final int NO_INTERVAL = 10;
    public static final int MINUTE_1 = 11;
    public static final int MINUTE_5 = 12;
    public static final int MINUTE_10 = 13;
    public static final int MINUTE_15 = 14;
    public static final int MINUTE_30 = 15;
    public static final int MINUTE_60 = 16;

    /**
     * Used to obtain valid API url for any daily, weekly, monthly, or intraday call with the given stock and interval.
     * @param _stock String with desired stock symbol. (Example: "IBM")
     * @param _interval String with desired time interval for Intraday (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     *                  Send in _interval as null if using anything except the intraday call.
     * @param _callType String used to select the type of API call to be made. (Options: "daily", "weekly", "monthly", "intraday")
     * @return A URL object ready to make an API call.
     * @throws MalformedURLException
     */
    public static URL getStockURL(String _stock, int _interval, int _callType) throws MalformedURLException {
        String function;
        String interval = "";
        String endpoint;

        //Translate the given interval to a string for the URL if the intraday call is being used
        if(_callType == INTRADAY_CALL)
            interval += "&interval=" + translateIntradayInterval(_interval);

        //API function being called
        function = translateStockCallType(_callType);

        return new URL(baseURLAlpha + "function=" + function + "&symbol=" + _stock + interval + "&apikey=" + apiKeyAlpha);

    }

    /**
     * Used to obtain valid API url for any daily, weekly, or monthly call with the given crypto currency and market.
     * @param _crypto String with desired crypto symbol. (Example: "BTC")
     * @param _market String with the desired market/currency to return data in. (Example: "USD")
     * @param _callType String used to select the type of API call to be made, intraday cannot be used with this call. (Options: "daily", "weekly", "monthly")
     * @return A URL object ready to make an API call.
     * @throws MalformedURLException
     */
    public static URL getCryptoURL(String _crypto, String _market, int _callType) throws MalformedURLException {
        String function;

        //API function being called
        if(_callType == INTRADAY_CALL){
            System.out.println("You cannot use an Intraday call on Crypto Currencies.");
            return null;
        }
        function = translateCryptoCallType(_callType);


        return new URL(baseURLAlpha + "function=" + function + "&symbol=" + _crypto + "&market=" + _market + "&apikey=" + apiKeyAlpha);
    }



    /**
     * Parses raw JSON information from API call and stores it in a JSONobject that is returned.
     * @param _APIlink URL object that is formatted for AlphaVantage API calls.
     * @return JSONobject containing intraday data.
     */
    protected static JSONObject getJSON(URL _APIlink) throws IOException, JSONException {
        // Create a HTTP Connection.
        HttpURLConnection con = (HttpURLConnection) _APIlink.openConnection();

        con.setRequestMethod("GET");

        // Examine the response code.
        int status = con.getResponseCode();
        if (status != 200) {
            System.out.print("Error: Could not load item: " + status);
            return null;
        } else {
            // Parsing input stream into a text string.
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            // Close the connections.
            in.close();
            con.disconnect();

            // Print out our complete JSON string. (For Testing)
            //System.out.println("Output: " + content.toString());

            return new JSONObject(content.toString());
        }
    }


    /**
     * Takes an intraday, daily, weekly, or monthly stock JSON and parses the data into entry objects. These objects are stored in an ArrayList and returned.
     * @param timeSeries JSONObject containing stock info for a specific type of API call.
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return ArrayList of stock Entries.
     */
    protected static ArrayList<Entry> parseStockJSON(JSONObject timeSeries, int _callType) throws JSONException, ParseException {
        //Create iterator to parse JSONObject
        Iterator<String> keys = timeSeries.keys();

        //Dynamic Arrays to hold values from JSON
        ArrayList<Entry> stockInfo = new ArrayList<>();

        String open, high, low, close, volume;
        DateFormat format;
        Date date;

        //Use Special format for Intraday call, otherwise use the default format.
        if(_callType == INTRADAY_CALL)
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
     * Takes a daily, weekly, or monthly crypto JSON and parses the data into entry objects. These objects are stored in an ArrayList and returned.
     * @param timeSeries JSONObject containing crypto info for a specific type of API call.
     * @param _market String with the desired market/currency to return data in. (Example: "USD")
     * @return ArrayList of crypto Entries.
     */
    protected static ArrayList<Entry> parseCryptoJSON(JSONObject timeSeries, String _market) throws JSONException, ParseException {
        //Create iterator to parse JSONObject
        Iterator<String> keys = timeSeries.keys();

        //Dynamic Arrays to hold values from JSON
        ArrayList<Entry> cryptoInfo = new ArrayList<>();

        String open, high, low, close, volume;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date;

        //Iterate the JSON and store all appropriate values in correct ArrayLists
        while(keys.hasNext()) {

            String key = keys.next();
            if (timeSeries.get(key) instanceof JSONObject) {
                open = ((JSONObject) timeSeries.get(key)).getString("1a. open (" + _market + ")");
                high = ((JSONObject) timeSeries.get(key)).getString("2a. high (" + _market + ")");
                low = ((JSONObject) timeSeries.get(key)).getString("3a. low (" + _market + ")");
                close = ((JSONObject) timeSeries.get(key)).getString("4a. close (" + _market + ")");
                volume = ((JSONObject) timeSeries.get(key)).getString("5. volume");
                Entry currentEntry = new Entry( Double.parseDouble(open),
                        Double.parseDouble(close),
                        Double.parseDouble(low),
                        Double.parseDouble(high),
                        (int)Double.parseDouble(volume),
                        format.parse(key) );
                cryptoInfo.add(currentEntry);
            }
        }
        Collections.sort(cryptoInfo);
        return cryptoInfo;
    }

    /**
     * This method is used to transform the int call type Enum from the AlphpaAPIDataGet, into a string to be used in parsing the stock JSON.
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return String usable for parsing fields in a stock JSON.
     */
    private static String translateStockCallType(int _callType){
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
                System.out.println("Not a valid Call Type.");
                return null;
        }
    }

    /**
     * This method is used to transform the int call type Enum from the AlphpaAPIDataGet, into a string to be used in parsing the crypto JSON.
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return String usable for parsing fields in a crypto JSON.
     */
    private static String translateCryptoCallType(int _callType){
        switch (_callType) {
            case DAILY_CALL:
                return "DIGITAL_CURRENCY_DAILY";

            case WEEKLY_CALL:
                return "DIGITAL_CURRENCY_WEEKLY";

            case MONTHLY_CALL:
                return "DIGITAL_CURRENCY_MONTHLY";

            default:
                //Returns NULL for error checking and reports error.
                System.out.println("Not a valid Call Type.");
                return null;
        }
    }

    /**
     * This method will transform the int interval Enum from the AlphpaAPIDataGet into a string to be used in the API URL.
     * @param _interval String with desired time interval, for Intraday only (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     *      *                  Use NO_INTERVAL for any call that is not intraday and this method will not be used in that case.
     * @return A formatted String ready for use in the API URL.
     */
    protected static String translateIntradayInterval(int _interval){
        //If using Intraday call then the interval must be checked so that the correct interval is used in the URL.
        switch(_interval) {
            case MINUTE_1:
                return "1min";

            case MINUTE_5:
                return "5min";

            case MINUTE_10:
                return "10min";

            case MINUTE_15:
                return "15min";

            case MINUTE_30:
                return "30min";

            case MINUTE_60:
                return "60min";

            default:
                System.out.println("Not a valid Interval.");
                return null;
        }
    }

    /**
     * This method will transform the int call type Enum from the AlphpaAPIDataGet into a string to be used in the API URL.
     * This method will take the callType used in the URL and return a correctly formatted String for parsing the JSON.
     * @param _callType Type of call String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    protected static String translateCryptoJSONInterval(int _callType){

        //All intervals start with this as a base string to be appended to.
        String base = "Time Series (Digital Currency ";

        switch(_callType) {
            case DAILY_CALL:
                return base + "Daily)";

            case WEEKLY_CALL:
                return base + "Weekly)";

            case MONTHLY_CALL:
                return base + "Monthly)";

            default:
                //Returns NULL for error checking and reports error.
                System.out.println("Not a valid Interval.");
                return null;
        }
    }

    /**
     * The time interval String used in the URL is different than the time interval String used to parse the JSON.
     * This method will take the interval used in the URL and return a correctly formatted interval String for parsing.
     * @param _rawInterval Time interval String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    protected static String translateStockJSONInterval(int _rawInterval){

        //All intervals start with this as a base string to be appended to.
        String base = "Time Series ";

        switch(_rawInterval) {
            case MINUTE_1:
                return base + "(1min)";

            case MINUTE_5:
                return base + "(5min)";

            case MINUTE_10:
                return base + "(10min)";

            case MINUTE_15:
                return base + "(15min)";

            case MINUTE_30:
                return base + "(30min)";

            case MINUTE_60:
                return base + "(60min)";

            case DAILY_CALL:
                return base + "(Daily)";

            case WEEKLY_CALL:
                return "Weekly Time Series";

            case MONTHLY_CALL:
                return "Monthly Time Series";

            default:
                //Returns NULL for error checking and reports error.
                System.out.println("Not a valid Interval.");
                return null;
        }
    }


}
