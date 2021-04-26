package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: Stocks model provides a constant access point for CwacosData to access stock data functions.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

import java.util.ArrayList;

public class Stocks {

    private static StocksAdapter adapter;

    public static void init(){
        adapter = new AVAPIStocksTranslator();
    }

    public static ArrayList<Entry> get(String _symbol, int _call_type, int _call_interval) {
        return adapter.getStocksData(_symbol, _call_type, _call_interval);
    }

    public static String[] getCallTypes() {
        return adapter.getCallTypes();
    }

    public static String[] getCallIntervals() {
        return adapter.getCallIntervals();
    }
}

