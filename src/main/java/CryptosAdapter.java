import java.util.ArrayList;

public interface CryptosAdapter {
    public ArrayList<Entry> getCryptoData(String _crypto, String _market, int _callType);
}
