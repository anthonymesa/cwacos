package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: DataSegment provides a template for all data segments that must be stored
 *      in the CwacosData class. The variables in this class are required for any
 *      financial data being worked with to be compatible with algorithms across Cwacos.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

import java.util.ArrayList;

public abstract class DataSegment {

    private String fileUrl = null;
    private String symbol;
    private int callType;
    private ArrayList<Entry> entryList;

    //================= GETTERS ===============

    public String getFileUrl() {
        return this.fileUrl;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getCallType() {
        return this.callType;
    }

    public ArrayList<Entry> getEntryList() {
        return this.entryList;
    }

    //================= SETTERS ===============

    public void setFileUrl(String _newUrl) {
        this.fileUrl = _newUrl;
    }

    public void setSymbol(String _symbol) {
        this.symbol = _symbol;
    }

    public void setCallType(int _callType) {
        this.callType = _callType;
    }

    public void setEntryList(ArrayList<Entry> _newList) {
        this.entryList = _newList;
    }
}