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
     */
    public static URL getStockURL(String _stock, int _interval, int _callType) throws MalformedURLException {
        String function;
        String interval = "&interval=";
        String endpoint;

        //If using Intraday call then the interval must be checked the correct interval is assigned.
        switch(_interval) {
            case NO_INTERVAL:
                interval = "";
                break;

            case MINUTE_1:
                interval += "1min";
                break;

            case MINUTE_5:
                interval += "5min";
                break;

            case MINUTE_10:
                interval += "10min";
                break;

            case MINUTE_15:
                interval += "15min";
                break;

            case MINUTE_30:
                interval += "30min";
                break;

            case MINUTE_60:
                interval += "60min";
                break;

            default:
                System.out.println("Not a valid Interval.");
                return null;
        }

        //API function being called
        switch (_callType) {
            case INTRADAY_CALL:
                function = "TIME_SERIES_INTRADAY";
                break;

            case DAILY_CALL:
                function = "TIME_SERIES_DAILY";
                break;

            case WEEKLY_CALL:
                function = "TIME_SERIES_WEEKLY";
                break;

            case MONTHLY_CALL:
                function = "TIME_SERIES_MONTHLY";
                break;

            default:
                //Returns NULL for error checking and reports error.
                System.out.println("Not a valid Call Type.");
                return null;
        }

        return new URL(baseURLAlpha + "function=" + function + "&symbol=" + _stock + interval + "&apikey=" + apiKeyAlpha);

    }

    /* TODO: Write this method */
    public static URL getCryptoURL(){
        return null;
    }

    /**
     * Parses raw JSON information from API call and stores it in a JSONobject that is returned.
     * @param _APIlink URL object that is formatted for AlphaVantage API calls.
     * @return JSONobject containing intraday data.
     */
    protected static JSONObject getStockJSON(URL _APIlink) throws IOException, JSONException {
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
     * The time interval String used in the URL is different than the time interval String used to parse the JSON.
     * This method will take the interval used in the URL and return a correctly formatted interval String for parsing.
     * @param _rawInterval Time interval String that was used to create your URL.
     * @return String containing time interval ready for use in JSON parsing.
     */
    protected static String translateInterval(int _rawInterval){

        //All interval start with this as a base string to be appended to.
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

    /**
     * Takes an intraday, daily, weekly, or monthly stock JSON and parses the data into entry objects. These objects are stored in an ArrayList and returned.
     * @param timeSeries JSONObject containing stock info for a specific type of API call.
     * @return ArrayList of stock Entries.
     */
    protected static ArrayList<Entry> parseStockJSON(JSONObject timeSeries) throws JSONException {
        //Create iterator to parse JSONObject
        Iterator<String> keys = timeSeries.keys();

        //Dynamic Arrays to hold values from JSON
        ArrayList<Entry> intraday = new ArrayList<>();

        String open, high, low, close, volume;

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
                                                key );
                intraday.add(currentEntry);
            }
        }

        return intraday;
    }


}
