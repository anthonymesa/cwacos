/*
    Project: Cwacos
    Author: Anthony Mesa
 */

import java.util.ArrayList;

// The purpose of this little class is so that
// we can package a stock/crypto's symbol, type
// and entry data together in a neat package.
public class FinanceDataSegment {

    String symbol;
    int data_type; // 0 = null, 1 = stock, 2 = crypto
    int call_type; // see apidataget
    int call_interval; // see apidataget
    ArrayList<Entry> data;

    FinanceDataSegment(String _symbol, int _data_type, int _call_type, int _call_interval){
        this.symbol = _symbol;
        this.data_type = _data_type;
        this.call_type = _call_type;
        this.call_interval = _call_interval;
    }
}
