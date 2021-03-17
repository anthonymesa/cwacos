package com.cwacos;
import org.json.*;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class APIClass {
    protected static final String baseURL = "https://www.alphavantage.co/query?";
    protected static final String apiKey = "MO9QU9JAPBBPX5T7";

    /**
     * Creates connection to API, stores JSON, parses JSON, and returns intraday data organized in an ArrayList of Entry Objects.
     * @param _stock String with desired stock symbol. (Example: "IBM")
     * @param _interval String with desired time interval for Intraday (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     * @return ArrayList of Entry objects.
     */
    public static ArrayList<Entry> getIntradayInfo(String _stock, String _interval){
        try {
            // Create the API url.
            URL url = getIntradayURL(_stock, _interval);

            // Save the main JSON file.
            JSONObject JSONFile = getStockJSON(url);

            //Translate the interval used in URL to correct format needed to parse the JSON file.
            String JSONinterval = translateInterval(_interval);

            //If there is an error receiving the JSONObject OR the time series interval then the function will exit.
            if(JSONFile == null || JSONinterval == null){
                return null;
            }

            //Create JSON object that will hold only the information for the given time interval.
            JSONObject timeSeries = JSONFile.getJSONObject(JSONinterval);

            //Parse the given interval JSON and save the data in an ArrayList of Entry objects.

            return parseStockJSON(timeSeries);
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex);
            return null;
        }

        /*for(int i = 0; i < intraday.size(); i++)
            System.out.println(intraday.get(i).toString());*/
    }

    /**
     * Used to obtain valid API url for an Intraday call with the given stock and interval.
     * @param _stock String with desired stock symbol. (Example: "IBM")
     * @param _interval String with desired time interval for Intraday (Options: "1min", "5min", "10min", "15min", "30min", "60min")
     * @return A URL object ready to make an API call.
     */
    public static URL getIntradayURL(String _stock, String _interval) throws MalformedURLException {
        //API function being called
        String function = "TIME_SERIES_INTRADAY";

        //Create endpoint and add it to the base URL to make API call
        String endpoint = "function=" + function + "&symbol=" + _stock + "&interval=" + _interval + "&apikey=" + apiKey;
        String urlString = baseURL + endpoint;

        return new URL(urlString);
    }

    /**
     * Parses raw JSON information from API call and stores it in a JSONobject that is returned.
     * @param _APIlink URL object that is formatted for AlphaVantage API calls.
     * @return JSONobject containing intraday data.
     */
    private static JSONObject getStockJSON(URL _APIlink) throws IOException, JSONException {
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
    private static String translateInterval(String _rawInterval){

        //All interval start with this as a base string to be appended to.
        String base = "Time Series ";

        switch(_rawInterval) {
            case "1min":
                return base + "(1min)";

            case "5min":
                return base + "(5min)";

            case "10min":
                return base + "(10min)";

            case "15min":
                return base + "(15min)";

            case "30min":
                return base + "(30min)";

            case "60min":
                return base + "(60min)";

            default:
                //Returns NULL for error checking and reports error.
                System.out.println("Not a valid Interval.");
                return null;
        }
    }

    /**
     * TODO: Generalize this method to be able to parse other types of API calls.
     * Takes an intraday stock JSON and parses the data into entry objects. These objects are stored and an ArrayList and returned.
     * @param timeSeries JSONobject containing Intraday info for a specific time interval.
     * @return ArrayList of intraday Entries.
     */
    private static ArrayList<Entry> parseStockJSON(JSONObject timeSeries) throws JSONException {
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
                Entry currentEntry = new Entry(Double.parseDouble(open), Double.parseDouble(close), Double.parseDouble(low), Double.parseDouble(high), Integer.parseInt(volume));
                intraday.add(currentEntry);
            }
        }

        return intraday;
    }

}