package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: CryptoDataSegment provides a DataSegment object customized for crypto calls. 
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

// The purpose of this little class is so that
// we can package a stock/crypto's symbol, type
// and entry data together in a neat package.
public class CryptoDataSegment extends DataSegment{
    
    private int callMarket;

    CryptoDataSegment(String _symbol, int _callType, int _callMarket) {
        super.setSymbol(_symbol);
        super.setCallType(_callType);
        this.callMarket = _callMarket;
    }

    //================= GETTERS ===============

    public int getCallMarket() {
        return this.callMarket;
    }

    //================= SETTERS ===============/

    public void setCallMarket(int _market) {
        this.callMarket = _market;
    }
}