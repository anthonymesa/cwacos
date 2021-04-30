package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: CryptosAdapter is an interface that defines the basic requirements for getting crypto
 *      information, no matter the method of retrieving data.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 *      Michael Leonard
 */

import java.util.ArrayList;

public interface CryptosAdapter {
    public ArrayList<Entry> getCryptoData(String _crypto, String _market, int _callType);
    public String[] getCallTypes();
    public String[] getCallMarkets();
}