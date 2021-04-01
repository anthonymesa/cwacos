
import java.util.ArrayList;
import java.util.Arrays;

public class Cwacos {
    public static void main(String[] args) {
        //Use the call below to print full intraday ArrayList for given stock and time interval. (5 calls/minute, 500 calls/day)

//        System.out.println("Intraday Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.MINUTE_5, AlphaAPIDataGet.INTRADAY_CALL).toString());
//        System.out.println("Daily Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.NO_INTERVAL, AlphaAPIDataGet.DAILY_CALL).toString());
//        System.out.println("Weekly Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.NO_INTERVAL, AlphaAPIDataGet.WEEKLY_CALL).toString());
//        System.out.println("Monthly Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.NO_INTERVAL, AlphaAPIDataGet.MONTHLY_CALL).toString());

        //CwacosUIController.beginUI(args);
        CwacosTester.begin();
    }
}