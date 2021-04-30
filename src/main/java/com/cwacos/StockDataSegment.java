package com.cwacos;

/**
 * Last updated: 26-APR-2021
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

    //==============================================================================
    // Getters
    //==============================================================================

    public int getCallInterval() {
        return this.callInterval;
    }

    //==============================================================================
    // Setters
    //==============================================================================

    public void setCallInterval(int _interval) {
        this.callInterval = _interval;
    }
}