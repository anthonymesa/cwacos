import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.*;

public class APIClass {

    public static void getIntradayInfo(String _stock){
        // Create a HTTP Connection.
        String baseUrl = "https://www.alphavantage.co/query?";

        //API function being called
        String function = "TIME_SERIES_INTRADAY";

        //Name of stock passed to this method
        String symbol = _stock;

        //Time between each price check by API
        String interval = "5min";

        //Personal API key
        String apiKey = "MO9QU9JAPBBPX5T7";

        //Create endpoint and add it to the base URL to make API call
        String endpoint = "function=" + function + "&symbol=" + symbol + "&interval=" + interval + "&apikey=" + apiKey;
        String urlString = baseUrl + endpoint;
        URL url;
        try {
            // Make the connection.
            url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
           // con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            con.setRequestMethod("GET");


            // Examine the response code.
            int status = con.getResponseCode();
            if (status != 200) {
                System.out.printf("Error: Could not load item: " + status);
            } else {
                // Parsing input stream into a text string.
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                // Close the connections.
                in.close();
                con.disconnect();
                // Print out our complete JSON string.
                System.out.println("Output: " + content.toString());

                // Parse that object into a usable Java JSON object. Also parse the JSON objects it contains
                JSONObject JSONFile = new JSONObject(content.toString());
                JSONObject timeSeries = JSONFile.getJSONObject("Time Series (5min)");

                //Create iterator to parse JSONObjects from "Time Series (5min)"
                Iterator<String> keys = timeSeries.keys();

                //Dynamic Arrays to hold values from JSON
                ArrayList<Entry> intraday = new ArrayList<>();

                String open="", high="", low="", close="", volume="";

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

                for(int i = 0; i < intraday.size(); i++)
                    System.out.println(intraday.get(i).toString());



            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            return;
        }
    }
}