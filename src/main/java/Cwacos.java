import org.json.*;

public class Cwacos {
    public static void main(String[] args) {
        //Use this call to print full intraday ArrayList for given stock and time interval. (5 calls/minute, 500 calls/day)
        System.out.println(APIClass.getIntradayInfo("IBM", "5min").toString());

        CwacosUI.beginUI(args);
    }
}