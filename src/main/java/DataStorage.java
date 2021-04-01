import java.util.ArrayList;

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
        }
    }

    /**
     * Saves the API output to a desired location.
     * @param _params ArrayList of Strings that will contain either the file URL/Path or the IP, Port number, etc.
     * @param _input  ArrayList of Entries to be stored to desired location.
     */
    public void save(ArrayList<String> _params, ArrayList<Entry> _input) {

        adapter.store(_params, arraylistOfEntriesToArraylistOfStrings(_input));
    }

    /**
     * Converts and returns an ArrayList of Entries to an ArrayList of Strings.
     * @param _arraylistOfEntries ArrayList of Entries to be converted to ArrayList of Strings.
     * @return ArrayList of Strings converted from ArrayList of Entries.
     */
    private ArrayList<String> arraylistOfEntriesToArraylistOfStrings(ArrayList<Entry> _arraylistOfEntries) {
        ArrayList<String> arraylistOfStrings = new ArrayList<>();
        for (Entry entry : _arraylistOfEntries) {

            arraylistOfStrings.add(entry.open + " " +
                    entry.close + " " +
                    entry.low + " " +
                    entry.high + " " +
                    entry.volume);
        }

        return arraylistOfStrings;
    }

    /**
     * Reads the stored data from a desired location.
     * @param _params ArrayList of Strings that will contain either the file URL/Path or the IP, Port number, etc.
     * @return ArrayList of Objects that contain the values read from the storage.
     */
    public ArrayList<Object> load(ArrayList<String> _params) {
        return adapter.load(_params);
    }
}