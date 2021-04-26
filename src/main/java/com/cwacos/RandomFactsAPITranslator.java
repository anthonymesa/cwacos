package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: RandomFactsAPITranslator
 * 
 * Contributing Authors:
 *      Michael Leonard
 */

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class RandomFactsAPITranslator implements QfactsAdapter {

    protected static final String baseURLQuokkas = "https://random-facts1.p.rapidapi.com/fact/search?query=quokka";
    protected static final String apiKeyQuokkas = "7428506839msh1141f5e6cf76abdp11775fjsn3ab3e1af2ad5";

    /**
     * Calls to Rapid API's server and accesses a random fact about Quokkas to be returned.
     * Limited to 5 calls per day.
     *
     * @return A string containing a fact about Quokkas.
     */
    private String getQuakkaFact() {
        try {
            //Build HttpRequest with correct headers and using our API key.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseURLQuokkas))
                    .header("x-rapidapi-key", apiKeyQuokkas)
                    .header("x-rapidapi-host", "random-facts1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            //Receive the response given by the HttpRequest
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //Store the response as JSON Object and parse through to the fact and return it
            JSONObject JSONFile = new JSONObject(response.body());
            JSONObject JSONQuokkasFact = JSONFile.getJSONObject("contents");
            return (JSONQuokkasFact.getString("fact"));
        } catch (Exception e) {
            System.out.println("Error!");
        }
        return null;
    }

    public ArrayList<String> getQfactsList(int _amnt){
        ArrayList<String> factsList = new ArrayList<String>();

        for(int i = 0; i < _amnt; i++){
            factsList.add(getQuakkaFact());
        }

        return factsList;
    }
}


