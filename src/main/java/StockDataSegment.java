/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

/*
    Project: Cwacos
    Author: Anthony Mesa
 */

// The purpose of this little class is so that
// we can package a stock/crypto's symbol, type
// and entry data together in a neat package.
public class StockDataSegment extends DataSegment{

    private int callInterval;

    StockDataSegment(String _symbol, int _callType, int _callInterval) {
        super.setSymbol(_symbol);
        super.setCallType(_callType);
        this.callInterval = _callInterval;
    }

    /* Getters */

    public int getCallInterval() {
        return this.callInterval;
    }

    /* Setters */

    public void setCallInterval(int _interval) {
        this.callInterval = _interval;
    }
}