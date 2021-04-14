import java.util.ArrayList;

public class Stocks {

    private static StocksAdapter adapter;

    public static void init(){
        adapter = new AVAPIStocksTranslator();
    }

    public static ArrayList<Entry> get(String _symbol, int _call_type, int _call_interval){
        return adapter.getStocksData(_symbol, _call_type, _call_interval);
    }
}
