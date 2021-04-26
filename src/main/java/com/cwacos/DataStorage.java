package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: DataStorage model provides a constant access point for CwacosData to access data storing functions.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 *      Hyoungjin Choi
 */

import java.util.ArrayList;
import java.util.Map;

class DataStorage {

    private static StorageAdapter adapter;

    public static void init() {
        adapter = new LocalStorageTranslator();
    }

    /**
     * Saves the API output to a desired location.
     *
     * @param _params ArrayList of Strings that will contain either the file URL/Path or the IP, Port number, etc.
     * @param _input  ArrayList of Entries to be stored to desired location.
     */
    public static Response save(ArrayList<String> _params, ArrayList<Entry> _input) {
        return adapter.store(_params, arraylistOfEntriesToArraylistOfStrings(_input));
    }

    /**
     * Reads the stored data from a desired location.
     *
     * @param _params ArrayList of Strings that will contain either the file URL/Path or the IP, Port number, etc.
     * @return ArrayList of Objects that contain the values read from the storage.
     */
    public static LoadData load(ArrayList<String> _params) throws Exception{
        try{
            return adapter.load(_params);
        } catch (Exception e) {
            throw e;
        }
    }

    public static Map<String, String> loadSettings(ArrayList<String> _params) {
        return adapter.loadSettings(_params);
    }

    public static void saveSettings(Map<String, String> _settings, ArrayList<String> _params) {
        adapter.saveSettings(_settings, _params);
    }

    /**
     * Converts and returns an ArrayList of Entries to an ArrayList of Strings.
     *
     * @param _arraylistOfEntries ArrayList of Entries to be converted to ArrayList of Strings.
     * @return ArrayList of Strings converted from ArrayList of Entries.
     */
    private static ArrayList<String> arraylistOfEntriesToArraylistOfStrings(ArrayList<Entry> _arraylistOfEntries) {
        ArrayList<String> arraylistOfStrings = new ArrayList<>();
        for (Entry entry : _arraylistOfEntries) {

            arraylistOfStrings.add(entry.toString());
        }

        return arraylistOfStrings;
    }
}