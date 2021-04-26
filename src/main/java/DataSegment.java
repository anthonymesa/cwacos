import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Info to be included for all datasegments
 */
public abstract class DataSegment {

    private String fileUrl = null;
    private String symbol;
    private int callType;
    private ArrayList<Entry> entryList;

    /* Getters */

    public String getFileUrl() {
        return this.fileUrl;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getCallType() {
        return this.callType;
    }

    public ArrayList<Entry> getEntryList() {
        return this.entryList;
    }

    /* Setters */

    public void setFileUrl(String _newUrl) {
        this.fileUrl = _newUrl;
    }

    public void setSymbol(String _symbol) {
        this.symbol = _symbol;
    }

    public void setCallType(int _callType) {
        this.callType = _callType;
    }

    public void setEntryList(ArrayList<Entry> _newList) {
        this.entryList = _newList;
    }
}
