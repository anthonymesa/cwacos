import java.util.ArrayList;

public class Qfacts {

    private static QfactsAdapter adapter;

    protected static void init(){
        adapter = new RandomFactsAPITranslator();
    }

    protected static ArrayList<String> getList(int _amnt){
        return adapter.getQfactsList(_amnt);
    }
}
