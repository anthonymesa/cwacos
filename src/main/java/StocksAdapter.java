import java.util.ArrayList;

public interface StocksAdapter {
    public ArrayList<Entry> getStocksData(String _symbol, int _call_type, int _call_interval);
    public String[] getCallTypes();
    public String[] getCallIntervals();
}
