package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: Cryptos model provides a constant access point for CwacosData to access crypto data functions.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 *      Michael Leonard
 */

import java.util.ArrayList;

public class Cryptos {

    /** Interface for Crypto implementation generalisation. */
    private static CryptosAdapter adapter;

    /** 
     * Initialize the adapter with the relevant translator.
     */
    protected static void init(){
        adapter = new AVAPICryptoTranslator();
    }

    protected static ArrayList<Entry> get(String _crypto, String _market, int _callType){
        return adapter.getCryptoData(_crypto, _market, _callType);
    }

    public static String[] getCallTypes() {
        return adapter.getCallTypes();
    }

    public static String[] getCallMarkets() {
        return adapter.getCallMarkets();
    }
}