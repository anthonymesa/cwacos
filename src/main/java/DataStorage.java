/*
Last updated:
Purpose of this class:
Contributing Authors: Anthony Mesa, Hyoungjin Choi
 */

import java.util.ArrayList;
import java.util.Map;

class DataStorage {

    public enum StorageType {LOCAL, DATABASE}

    StorageAdapter adapter;

    /* constructor takes enum, if LOCAL is used as
     * a parameter, initializes storage adapter
     * as LocalFileStorage class
     */
    public DataStorage(StorageType _type) {
        if (_type == StorageType.LOCAL) {
            adapter = new LocalStorageTranslator();
        } else if (_type == StorageType.DATABASE) {
            System.out.println("This feature is not yet supported.");
        } else {
            System.out.println("Invalid input!");
        }
    }

    /**
     * Saves the API output to a desired location.
     *
     * @param _params ArrayList of Strings that will contain either the file URL/Path or the IP, Port number, etc.
     * @param _input  ArrayList of Entries to be stored to desired location.
     */
    public void save(ArrayList<String> _params, ArrayList<Entry> _input) {
        if (_params.isEmpty()) {
            throw new IllegalArgumentException("Storage parameter can not be empty!");
        } else if (_input.isEmpty()) {
            throw new IllegalArgumentException("Input parameter can not be empty!");
        } else {
            adapter.store(_params, arraylistOfEntriesToArraylistOfStrings(_input));
        }
    }

    /**
     * Reads the stored data from a desired location.
     *
     * @param _params ArrayList of Strings that will contain either the file URL/Path or the IP, Port number, etc.
     * @return ArrayList of Objects that contain the values read from the storage.
     */
    public ArrayList<Object> load(ArrayList<String> _params) {
        return adapter.load(_params);
    }

    public Map<String, String> loadSettings(ArrayList<String> _params) {
        return adapter.loadSettings(_params);
    }

    public void saveSettings(Map<String, String> _settings, ArrayList<String> _params) {
        adapter.saveSettings(_settings, _params);
    }

    /**
     * Converts and returns an ArrayList of Entries to an ArrayList of Strings.
     *
     * @param _arraylistOfEntries ArrayList of Entries to be converted to ArrayList of Strings.
     * @return ArrayList of Strings converted from ArrayList of Entries.
     */
    private ArrayList<String> arraylistOfEntriesToArraylistOfStrings(ArrayList<Entry> _arraylistOfEntries) {
        ArrayList<String> arraylistOfStrings = new ArrayList<>();
        for (Entry entry : _arraylistOfEntries) {
            arraylistOfStrings.add(entry.toString());
        }
        return arraylistOfStrings;
    }
}