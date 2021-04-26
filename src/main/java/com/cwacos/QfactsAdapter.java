package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: QfactsAdapter is an interface that defines the basic requirements for getting quakka facts,
 *      no matter the method of retrieving data.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

import java.util.ArrayList;

public interface QfactsAdapter {
    public ArrayList<String> getQfactsList(int _amnt);
}
