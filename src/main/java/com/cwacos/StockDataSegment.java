package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: StockDataSegment provides a DataSegment object customized for stock calls. 
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

public class StockDataSegment extends DataSegment{

    private int callInterval;

    StockDataSegment(String _symbol, int _callType, int _callInterval) {
        super.setSymbol(_symbol);
        super.setCallType(_callType);
        this.callInterval = _callInterval;
    }

    //================= GETTERS ===============

    public int getCallInterval() {
        return this.callInterval;
    }

    //================= SETTERS ===============

    public void setCallInterval(int _interval) {
        this.callInterval = _interval;
    }
}