import org.json.*;

import java.net.MalformedURLException;

public class Cwacos {
    public static void main(String[] args){
        //Use the call below to print full intraday ArrayList for given stock and time interval. (5 calls/minute, 500 calls/day)
        System.out.println("Intraday Data: " + IntradayAPI.getIntradayInfo("IBM", "5min").toString());
        System.out.println("Daily Data: " + DailyAPI.getDailyInfo("IBM").toString());
        System.out.println("Weekly Data: " + WeeklyAPI.getWeeklyInfo("IBM").toString());
        System.out.println("Monthly Data: " + MonthlyAPI.getMonthlyInfo("IBM").toString());

        /*
        Use the call below to print a random fact about Quokkas.
        CAREFUL! There only 5 calls/day on this API and I (Michael Leonard) get charged $.03 for each additional call.
        */
        //System.out.println(QuokkasAPI.getQuokkasFact());

        CwacosUI.beginUI(args);
    }
}