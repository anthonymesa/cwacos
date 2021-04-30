package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: This class handles calls to the Randoms Facts API, as well as storing and exporting the facts it
 *          receives in a fact list. Currently only used to make calls about Quokkas.
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
     * This method will make a single call to the Random Facts API to request a random fact about Quokkas.
     * It will return the fact it receives in a String.
     * Limited to 5 calls per day.
     *
     * @return A string containing a fact about Quokkas.
     */
    private String getQuokkaFact() {
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
            return null;
        }

    }

    /**
     * This method will create an ArrayList of Strings used to hold facts about Quokkas.
     * It will make a predefined number calls to the Random Facts API to and store each
     * fact in the ArrayList separately, then return the list.
     * @param _amnt The number of facts to be placed into the list of facts.
     * @return  An ArrayList of Strings where each String is a fact about Quokkas.
     */
    public ArrayList<String> getQfactsList(int _amnt){
        ArrayList<String> factsList = new ArrayList<String>();
        String fact;

        for(int i = 0; i < _amnt; i++){
            if((fact = getQuokkaFact()) != null)
                factsList.add(fact);
            else
                //If the API call fails at any point, then return an empty ArrayList
                return new ArrayList<String>();
        }

        return factsList;
    }
}