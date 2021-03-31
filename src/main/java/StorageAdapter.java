import java.util.ArrayList;

/* be aware that Load returns an array list of Object type, the elements in this
 * array list will need to be casted to Entry type when they are being read out
 * of the list in the CwacosData class!
 */
interface StorageAdapter {
    void store(ArrayList<String> params, ArrayList<String> input);
    ArrayList<Object> load(ArrayList<String> params);
} 