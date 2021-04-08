/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

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

public class AlphaVantageAPITranslator {

    protected static final String baseURLAlpha = "https://www.alphavantage.co/query?";
    protected static final String apiKeyAlpha = "MO9QU9JAPBBPX5T7";

    public enum CallType {INTRADAY_CALL, DAILY_CALL, WEEKLY_CALL, MONTHLY_CALL}

    public enum CallInterval {NO_INTERVAL, MINUTE_1, MINUTE_5, MINUTE_10, MINUTE_15, MINUTE_30, MINUTE_60}

    /**
     * Creates connection to API, stores JSON, parses JSON, and returns data organized in an ArrayList of Entry Objects.
     * .    * Limited to 5 calls per minute and 500 calls per day.
     *
     * @param _stock    String with desired stock symbol. (Example: "IBM")
     * @param _callType Int from 1 to 4 where,
     *                  1 = Intraday, 2 = Daily, 3 = Weekly, 4 = Monthly
     * @param _interval Int from 10 to 16 where,
     *                  10 = No interval, 11 = One Minute, 12 = Five Minutes, 13 = Ten Minutes, 14 = Fifteen Minutes, 15 = Thirty Minutes, 16 = One Hour
     * @return ArrayList of Entry objects.
     */
    public static ArrayList<Entry> getStockInfo(String _stock, int _callType, int _interval) {
        try {
            //Translate the given call and interval into Enums
            CallType callEnum = translateTypeEnum(_callType);
            CallInterval intervalEnum = translateIntervalEnum(_interval);

            // Create the API url.
            URL url = getStockURL(_stock, intervalEnum, callEnum);

            // Save the main JSON file.
            JSONObject JSONFile = getJSON(url);

            //Translate the interval used in URL to correct format needed to parse the JSON file.
            String JSONInterval;
            if (intervalEnum == CallInterval.NO_INTERVAL)
                JSONInterval = translateStockJSONInterval(callEnum);
            else
                JSONInterval = translateStockJSONInterval(callEnum);

            //If there is an error receiving the JSONObject OR the time series interval then the function will exit.
            if (JSONFile == null || JSONInterval == null)
                return null;

            //Create JSON object that will hold only the information for the given time interval.
            JSONObject timeSeries = JSONFile.getJSONObject(JSONInterval);

            //Parse the given interval JSON and return the data in an ArrayList of Entry objects.
            return parseStockJSON(timeSeries, callEnum);
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            return null;
        }
    }

    /**
     * Creates connection to API, stores JSON, parses JSON, and returns intraday data organized in an ArrayList of Entry Objects.
     * .    * Limited to 5 calls per minute and 500 calls per day.
     *
     * @param _crypto   String with desired crypto symbol. (Example: "BTC")
     * @param _market   String with the desired market/currency to return data in. (Example: "USD")
     * @param _callType Int from 2 to 4 where,
     *                  *                       2 = Daily, 3 = Weekly, 4 = Monthly
     *                  **NOTE** Intraday call cannot be made on a Crypto Symbol.
     * @return ArrayList of Entry objects.
     */
    public static ArrayList<Entry> getCryptoInfo(String _crypto, String _market, int _callType) {
        try {
            //Translate the given call into Enum
            CallType callEnum = translateTypeEnum(_callType);

            // Create the API url
            URL url = getCryptoURL(_crypto, _market, callEnum);

            // Save the main JSON file
            JSONObject JSONFile = getJSON(url);

            //Translate the callType used in URL to correct format needed to parse the JSON file.
            String JSONInterval = translateCryptoJSONInterval(callEnum);

            //If there is an error receiving the JSONObject OR the time series interval then the function will exit.
            if (JSONFile == null || JSONInterval == null)
                return null;

            //Create JSON object that will hold only the information for the given time interval.
            JSONObject timeSeries = JSONFile.getJSONObject(JSONInterval);

            return parseCryptoJSON(timeSeries, _market);
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            return null;
        }
    }

    /**
     * Used to obtain valid API url for any daily, weekly, monthly, or intraday call with the given stock and interval.
     *
     * @param _stock    String with desired stock symbol. (Example: "IBM")
     * @param _interval String with desired time interval for Intraday (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     *                  Send in _interval as null if using anything except the intraday call.
     * @param _callType String used to select the type of API call to be made. (Options: "daily", "weekly", "monthly", "intraday")
     * @return A URL object ready to make an API call.
     * @throws MalformedURLException
     */
    private static URL getStockURL(String _stock, CallInterval _interval, CallType _callType) throws MalformedURLException {
        String function;
        String interval = "";
        String endpoint;

        //Translate the given interval to a string for the URL if the intraday call is being used
        if (_callType == CallType.INTRADAY_CALL)
            interval += "&interval=" + translateIntradayInterval(_interval);

        //API function being called
        function = translateStockCallType(_callType);

        return new URL(baseURLAlpha + "function=" + function + "&symbol=" + _stock + interval + "&apikey=" + apiKeyAlpha);
    }

    /**
     * Used to obtain valid API url for any daily, weekly, or monthly call with the given crypto currency and market.
     *
     * @param _crypto   String with desired crypto symbol. (Example: "BTC")
     * @param _market   String with the desired market/currency to return data in. (Example: "USD")
     * @param _callType String used to select the type of API call to be made, intraday cannot be used with this call. (Options: "daily", "weekly", "monthly")
     * @return A URL object ready to make an API call.
     * @throws MalformedURLException
     */
    private static URL getCryptoURL(String _crypto, String _market, CallType _callType) throws MalformedURLException {
        String function;

        //API function being called
        if (_callType == CallType.INTRADAY_CALL) {
            System.out.println("You cannot use an Intraday call on Crypto Currencies.");
            return null;
        }
        function = translateCryptoCallType(_callType);

        return new URL(baseURLAlpha + "function=" + function + "&symbol=" + _crypto + "&market=" + _market + "&apikey=" + apiKeyAlpha);
    }

    /**
     * Parses raw JSON information from API call and stores it in a JSONobject that is returned.
     *
     * @param _APIlink URL object that is formatted for AlphaVantage API calls.
     * @return JSONobject containing intraday data.
     */
    private static JSONObject getJSON(URL _APIlink) throws IOException, JSONException {
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
     *
     * @param _timeSeries JSONObject containing stock info for a specific type of API call.
     * @param _callType   Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return ArrayList of stock Entries.
     */
    private static ArrayList<Entry> parseStockJSON(JSONObject _timeSeries, CallType _callType) throws JSONException, ParseException {
        //Create iterator to parse JSONObject
        Iterator<String> keys = _timeSeries.keys();

        //Dynamic Arrays to hold values from JSON
        ArrayList<Entry> stockInfo = new ArrayList<>();

        String open, high, low, close, volume;
        DateFormat format;
        Date date;

        //Use Special format for Intraday call, otherwise use the default format.
        if (_callType == CallType.INTRADAY_CALL)
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        else
            format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        //Iterate the JSON and store all appropriate values in correct ArrayLists
        while (keys.hasNext()) {

            String key = keys.next();
            if (_timeSeries.get(key) instanceof JSONObject) {
                open = ((JSONObject) _timeSeries.get(key)).getString("1. open");
                high = ((JSONObject) _timeSeries.get(key)).getString("2. high");
                low = ((JSONObject) _timeSeries.get(key)).getString("3. low");
                close = ((JSONObject) _timeSeries.get(key)).getString("4. close");
                volume = ((JSONObject) _timeSeries.get(key)).getString("5. volume");
                Entry currentEntry = new Entry(Double.parseDouble(open),
                        Double.parseDouble(close),
                        Double.parseDouble(low),
                        Double.parseDouble(high),
                        Integer.parseInt(volume),
                        format.parse(key));
                stockInfo.add(currentEntry);
            }
        }
        Collections.sort(stockInfo);
        return stockInfo;
    }

    /**
     * Takes a daily, weekly, or monthly crypto JSON and parses the data into entry objects. These objects are stored in an ArrayList and returned.
     *
     * @param _timeSeries JSONObject containing crypto info for a specific type of API call.
     * @param _market     String with the desired market/currency to return data in. (Example: "USD")
     * @return ArrayList of crypto Entries.
     */
    private static ArrayList<Entry> parseCryptoJSON(JSONObject _timeSeries, String _market) throws JSONException, ParseException {
        //Create iterator to parse JSONObject
        Iterator<String> keys = _timeSeries.keys();

        //Dynamic Arrays to hold values from JSON
        ArrayList<Entry> cryptoInfo = new ArrayList<>();

        String open, high, low, close, volume;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date;

        //Iterate the JSON and store all appropriate values in correct ArrayLists
        while (keys.hasNext()) {

            String key = keys.next();
            if (_timeSeries.get(key) instanceof JSONObject) {
                open = ((JSONObject) _timeSeries.get(key)).getString("1a. open (" + _market + ")");
                high = ((JSONObject) _timeSeries.get(key)).getString("2a. high (" + _market + ")");
                low = ((JSONObject) _timeSeries.get(key)).getString("3a. low (" + _market + ")");
                close = ((JSONObject) _timeSeries.get(key)).getString("4a. close (" + _market + ")");
                volume = ((JSONObject) _timeSeries.get(key)).getString("5. volume");
                Entry currentEntry = new Entry(Double.parseDouble(open),
                        Double.parseDouble(close),
                        Double.parseDouble(low),
                        Double.parseDouble(high),
                        (int) Double.parseDouble(volume),
                        format.parse(key));
                cryptoInfo.add(currentEntry);
            }
        }
        Collections.sort(cryptoInfo);
        return cryptoInfo;
    }

    /**
     * This method will take the int given by the caller and return the appropriate enum variable to continue processing the call.
     *
     * @param _callType Int from 1 to 4
     * @return CallType enum
     */
    private static CallType translateTypeEnum(int _callType) {
        switch (_callType) {
            case 1:
                return CallType.INTRADAY_CALL;

            case 2:
                return CallType.DAILY_CALL;

            case 3:
                return CallType.WEEKLY_CALL;

            case 4:
                return CallType.MONTHLY_CALL;

            default:
                System.out.println("Invalid call type.");
                return null;
        }
    }

    /**
     * This method will take the int given by the caller and return the appropriate enum variable to continue processing the call.
     *
     * @param _callInterval Int from 10 to 16
     * @return CallInterval enum
     */
    private static CallInterval translateIntervalEnum(int _callInterval) {
        switch (_callInterval) {
            case 10:
                return CallInterval.NO_INTERVAL;

            case 11:
                return CallInterval.MINUTE_1;

            case 12:
                return CallInterval.MINUTE_5;

            case 13:
                return CallInterval.MINUTE_10;

            case 14:
                return CallInterval.MINUTE_15;

            case 15:
                return CallInterval.MINUTE_30;

            case 16:
                return CallInterval.MINUTE_60;

            default:
                System.out.println("Invalid call type.");
                return null;
        }
    }

    /**
     * This method is used to transform the int call type Enum from the AlphpaAPIDataGet, into a string to be used in parsing the stock JSON.
     *
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return String usable for parsing fields in a stock JSON.
     */
    private static String translateStockCallType(CallType _callType) {
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
     *
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return String usable for parsing fields in a crypto JSON.
     */
    private static String translateCryptoCallType(CallType _callType) {
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
     *
     * @param _interval String with desired time interval, for Intraday only (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     *                  *                  Use NO_INTERVAL for any call that is not intraday and this method will not be used in that case.
     * @return A formatted String ready for use in the API URL.
     */
    private static String translateIntradayInterval(CallInterval _interval) {
        //If using Intraday call then the interval must be checked so that the correct interval is used in the URL.
        switch (_interval) {
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
     *
     * @param _callType Type of call String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    private static String translateCryptoJSONInterval(CallType _callType) {

        //All intervals start with this as a base string to be appended to.
        String base = "Time Series (Digital Currency ";

        switch (_callType) {
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
     *
     * @param _rawInterval Time interval String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    private static String translateStockJSONInterval(CallInterval _rawInterval) {

        //All intervals start with this as a base string to be appended to.
        String base = "Time Series ";

        switch (_rawInterval) {
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

            default:
                //Returns NULL for error checking and reports error.
                System.out.println("Not a valid Interval.");
                return null;
        }
    }

    /**
     * The time interval String used in the URL is different than the time interval String used to parse the JSON.
     * This method will take the interval used in the URL and return a correctly formatted interval String for parsing.
     *
     * @param _rawInterval Time interval String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    private static String translateStockJSONInterval(CallType _rawInterval) {

        //All intervals start with this as a base string to be appended to.
        String base = "Time Series ";

        switch (_rawInterval) {
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
