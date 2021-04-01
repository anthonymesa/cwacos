import java.util.ArrayList;
import java.util.Arrays;

public class Cwacos {
    public static void main(String[] args){
        // Use the call below to print full intraday ArrayList for given stock and time interval. (5 calls/minute, 500 calls/day)
//        System.out.println(APIClass.getIntradayInfo("IBM", "5min").toString());
//        IO.storeData(APIClass.getIntradayInfo("IBM", "5min"));

        // instantiate data storage
        DataStorage data = new DataStorage(DataStorage.StorageType.LOCAL);

        data.save(new ArrayList<String>(Arrays.asList("res/data.txt")), APIClass.getIntradayInfo("IBM", "5min"));
        ArrayList<Object> read = data.load(new ArrayList<String>(Arrays.asList("res/data.txt")));

        /*
        Use the call below to print a random fact about Quokkas.
        CAREFUL! There only 5 calls/day on this API and I (Michael Leonard) get charged $.03 for each additional call.
        */
        //System.out.println(APIClass.getQuokkasFact());
        CwacosUI.startUI(args);
    }
}