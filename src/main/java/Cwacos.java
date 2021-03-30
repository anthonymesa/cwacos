import org.json.*;
import java.net.MalformedURLException;

public class Cwacos {
    public static void main(String[] args){
        //Use the call below to print full intraday ArrayList for given stock and time interval. (5 calls/minute, 500 calls/day)

        System.out.println("Intraday Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.MINUTE_5, AlphaAPIDataGet.INTRADAY_CALL).toString());
        System.out.println("Daily Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.NO_INTERVAL, AlphaAPIDataGet.DAILY_CALL).toString());
        System.out.println("Weekly Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.NO_INTERVAL, AlphaAPIDataGet.WEEKLY_CALL).toString());
        System.out.println("Monthly Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.NO_INTERVAL, AlphaAPIDataGet.MONTHLY_CALL).toString());

        IO.storeData(AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.MINUTE_5, AlphaAPIDataGet.INTRADAY_CALL));


        /*
        Use the call below to print a random fact about Quokkas.
        CAREFUL! There only 5 calls/day on this API and I (Michael Leonard) get charged $.03 for each additional call.
        */
        //System.out.println(RandomFactsAPITranslator.getQuokkasFact());

        CwacosUI.beginUI(args);
    }
}