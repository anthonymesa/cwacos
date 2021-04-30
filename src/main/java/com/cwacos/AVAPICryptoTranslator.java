package com.cwacos;

/**
 * Last updated: 30-APR-2021
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

public class AVAPICryptoTranslator extends AlphaVantageConnection implements CryptosAdapter{

    /**
     * Creates connection to API, stores JSON, parses JSON, and returns intraday data organized in an ArrayList of Entry Objects.
     * 
     * There is a limit on calls that can be made for AlphaVantage; 5 calls per minute and up to 500 calls per day.
     *
     * @param _crypto String with desired crypto symbol. (Example: "BTC")
     * @param _market String with the desired market/currency to return data in. (Example: "USD")
     * @param _callType Int from 2 to 4 where,
     *      *                       2 = Daily, 3 = Weekly, 4 = Monthly
     *                              **NOTE** Intraday call cannot be made on a Crypto Symbol.
     * @return ArrayList of Entry objects.
     */
    public ArrayList<Entry> getCryptoData(String _crypto, String _market, int _callType) {
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
        }
        catch (Exception ex) {
            return null;
        }
   }

    /**
     * Used to obtain valid API url for any daily, weekly, or monthly call with the given crypto currency and market.
     * @param _crypto String with desired crypto symbol. (Example: "BTC")
     * @param _market String with the desired market/currency to return data in. (Example: "USD")
     * @param _callType String used to select the type of API call to be made, intraday cannot be used with this call. (Options: "daily", "weekly", "monthly")
     * @return A URL object ready to make an API call.
     * @throws MalformedURLException
     */
    private static URL getCryptoURL(String _crypto, String _market, CallType _callType) throws MalformedURLException {
        String function;

        //API function being called
        if(_callType == CallType.INTRADAY_CALL){
            return null;
        }
        function = translateCryptoCallType(_callType);


        return new URL(baseURLAlpha + "function=" + function + "&symbol=" + _crypto + "&market=" + _market + "&apikey=" + apiKeyAlpha);
    }

    /**
     * Takes a daily, weekly, or monthly crypto JSON and parses the data into entry objects. These objects are stored in an ArrayList and returned.
     * @param timeSeries JSONObject containing crypto info for a specific type of API call.
     * @param _market String with the desired market/currency to return data in. (Example: "USD")
     * @return ArrayList of crypto Entries.
     */
    private static ArrayList<Entry> parseCryptoJSON(JSONObject timeSeries, String _market) throws JSONException, ParseException {
        //Create iterator to parse JSONObject
        Iterator<String> keys = timeSeries.keys();

        //Dynamic Arrays to hold values from JSON
        ArrayList<Entry> cryptoInfo = new ArrayList<>();

        String open, high, low, close, volume;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

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
     * This method is used to transform the int call type Enum from the AlphpaAPIDataGet, into a string to be used in parsing the crypto JSON.
     * @param _callType Int associated with the type of call Enums in the AlphaAPIDataGet Class
     * @return String usable for parsing fields in a crypto JSON.
     */
    private static String translateCryptoCallType(CallType _callType){
        switch (_callType) {
            case DAILY_CALL:
                return "DIGITAL_CURRENCY_DAILY";

            case WEEKLY_CALL:
                return "DIGITAL_CURRENCY_WEEKLY";

            case MONTHLY_CALL:
                return "DIGITAL_CURRENCY_MONTHLY";

            default:
                //Returns NULL for error
                return null;
        }
    }

    /**
     * This method will transform the int call type Enum from the AlphpaAPIDataGet into a string to be used in the API URL.
     * This method will take the callType used in the URL and return a correctly formatted String for parsing the JSON.
     * @param _callType Type of call String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    private static String translateCryptoJSONInterval(CallType _callType){

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
                //Returns NULL for error checking
                return null;
        }
    }

    public String[] getCallTypes() {
        return new String[] { "Daily", "Weekly", "Monthly" };
    }

    public String[] getCallMarkets() {
        return new String[] { "USD" };
    }
}