package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: StocksAdapter is an interface that defines the basic requirements for getting stocks
 *          information, no matter the method of retrieving data.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 *      Michael Leonard
 */

import java.util.ArrayList;

public interface StocksAdapter {
    public ArrayList<Entry> getStocksData(String _symbol, int _call_type, int _call_interval);
    public String[] getCallTypes();
    public String[] getCallIntervals();
}