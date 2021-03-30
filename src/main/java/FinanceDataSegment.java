/*
    Project: Cwacos
    Author: Anthony Mesa
 */

import java.util.ArrayList;

// The purpose of this little class is so that
// we can package a stock/crypto's symbol, type
// and entry data together in a neat package.
public class FinanceDataSegment {

    public enum CallType { NULL, STOCK, CRYPTO };

    String symbol;
    CallType call_type;
    ArrayList<Entry> data;

    FinanceDataSegment(String s, CallType t){
        this.symbol = s;
        this.call_type = t;
    }
}
