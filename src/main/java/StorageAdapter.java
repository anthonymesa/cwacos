/*
Last updated:
Purpose of this class:
Contributing Authors: Anthony Mesa
 */

import java.util.ArrayList;
import java.util.Map;

/* be aware that Load returns an array list of Object type, the elements in this
 * array list will need to be casted to Entry type when they are being read out
 * of the list in the CwacosData class!
 */
interface StorageAdapter {
    void store(ArrayList<String> _params, ArrayList<String> _input);

    ArrayList<Object> load(ArrayList<String> _params);

    Map<String, String> loadSettings(ArrayList<String> _params);

    void saveSettings(Map<String, String> _settings, ArrayList<String> _params);
} 