import java.util.ArrayList;

public class Cryptos {

    private static CryptosAdapter adapter;

    protected static void init(){
        adapter = new AVAPICryptoTranslator();
    }

    protected static ArrayList<Entry> get(String _crypto, String _market, int _callType){
        return adapter.getCryptoData(_crypto, _market, _callType);
    }

    public static String[] getCallTypes() {
        return adapter.getCallTypes();
    }

    public static String[] getCallMarkets() {
        return adapter.getCallMarkets();
    }
}
