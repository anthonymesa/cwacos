import org.json.*;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cwacos {
    public static void main(String[] args) {
        //Use the call below to print full intraday ArrayList for given stock and time interval. (5 calls/minute, 500 calls/day)

        System.out.println("Intraday Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.INTRADAY_CALL, AlphaAPIDataGet.MINUTE_5).toString());
        System.out.println("Daily Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.DAILY_CALL, AlphaAPIDataGet.NO_INTERVAL).toString());
        System.out.println("Weekly Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.WEEKLY_CALL, AlphaAPIDataGet.NO_INTERVAL).toString());
        System.out.println("Monthly Data: " + AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.MONTHLY_CALL, AlphaAPIDataGet.NO_INTERVAL).toString());

        IO.storeData(AlphaVantageAPITranslator.getStockInfo("IBM", AlphaAPIDataGet.INTRADAY_CALL, AlphaAPIDataGet.MINUTE_5));


        /*
        Use the call below to print a random fact about Quokkas.
        CAREFUL! There only 5 calls/day on this API and I (Michael Leonard) get charged $.03 for each additional call.
        */
        //System.out.println(RandomFactsAPITranslator.getQuokkasFact());

        CwacosUI.beginUI(args);

    }
}