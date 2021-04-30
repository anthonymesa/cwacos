package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: Qfacts model provides a constant access point for CwacosData to access quakka facts data functions.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

import java.util.ArrayList;

public class Qfacts {

    private static QfactsAdapter adapter;

    protected static void init(){
        adapter = new RandomFactsAPITranslator();
    }

    protected static ArrayList<String> getList(int _amnt){
        return adapter.getQfactsList(_amnt);
    }
}