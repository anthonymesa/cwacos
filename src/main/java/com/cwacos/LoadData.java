package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: LoadData is a container for passing loaded data from the LocalFileTranslator to the
 *      CwacosData class. This is necessary because the data, symbol and data type must be
 *      returned all at once when a file is loaded.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

import java.util.ArrayList;

public class LoadData {

    private ArrayList<Entry> data;
    private String symbol;
    private int dataType;

    LoadData(ArrayList<Object> _data, String _symbol, int _type) {
        this.data = castToEntry(_data);
        this.symbol = _symbol;
        this.dataType = _type;
    }

    /* This is for casting the objects returned into
     * entry objects. If we ever change Entry or use a
     * different type in the future, this is the only
     * thing we need to change.
     */
    private ArrayList<Entry> castToEntry(ArrayList<Object> _data) {
        ArrayList<Entry> loadedData = new ArrayList<>();

        for (Object each : _data) {
            loadedData.add((Entry) each);
        }

        return loadedData;
    }

    //================= GETTERS ===============

    public ArrayList<Entry> getData() {
        return this.data;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getDataType() {
        return this.dataType;
    }
}