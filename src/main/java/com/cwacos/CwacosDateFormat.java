package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: CwacosDateFormat allows a static date format to be defined and accessed
 *      from anywhere in the com.cwacos package.
 * 
 * Contributing Authors:
 *      Hyoungjin Choi
 */

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CwacosDateFormat {
    public static  SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
    }
}
