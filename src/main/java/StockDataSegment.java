/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

/*
    Project: Cwacos
    Author: Anthony Mesa
 */

import java.util.ArrayList;

// The purpose of this little class is so that
// we can package a stock/crypto's symbol, type
// and entry data together in a neat package.
public class StockDataSegment {

    String symbol;
    int call_type; // see apidataget
    int call_interval; // see apidataget
    String url;
    ArrayList<Entry> data;

    StockDataSegment(String _symbol, int _call_type, int _call_interval) {
        this.symbol = _symbol;
        this.call_type = _call_type;
        this.call_interval = _call_interval;
        this.url = "";
    }
}