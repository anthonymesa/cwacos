import java.util.ArrayList;
import java.util.Map;

public class CwacosData {

    // This map represents a mapped list of favorited data
    // Map of strings to Ticker objects
    // e.g. "GME" --> new Ticker(s, t, d)
    private static Map<String, FinanceDataSegment> finance_data;

    // the only purpose of this is to turn a string interpretation
    // of the enum into an actual enum. I am using enums here
    // so we dont have to do any string validation beyond this later on.
    public static FinanceDataSegment.CallType GetCallTypeFromString(String input){
        switch(input){
            case "stock":
                return FinanceDataSegment.CallType.STOCK;
            case "crypto":
                return FinanceDataSegment.CallType.CRYPTO;
            default:
                return FinanceDataSegment.CallType.NULL;
        }
    }

    // when a user clicks add favorite, a dialogue window should pop up
    // that lets them put in a stock ticker and choose whether it is
    // stock or it is crypto, then this function should be run on success.
    public static void AddFavorite(String ticker_symbol, String type_string){
        if(/* validate ticker with api call */ true){
            FinanceDataSegment.CallType call_type = GetCallTypeFromString(type_string);
            finance_data.putIfAbsent(ticker_symbol, new FinanceDataSegment(ticker_symbol, call_type));
        } else {
            System.out.println("EROR: CwacosData.AddFavorite - New favorite failed validation");
        }
    }

    public static void RemoveFavorite(String ticker_symbol){
        finance_data.remove(ticker_symbol);
    }


}
