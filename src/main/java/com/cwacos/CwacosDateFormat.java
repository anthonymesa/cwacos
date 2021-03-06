package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: CwacosDateFormat allows a static date format to be defined and accessed
 *      from anywhere in the com.cwacos package.
 * 
 * Contributing Authors:
 *      Hyoungjin Choi
 *      Michael Leonard
 */

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CwacosDateFormat {
    public static  SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);
    }

    public static SimpleDateFormat getHumanReadableDateFormat(){
        return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
    }

    public static  SimpleDateFormat getGraphFormat() {
        return new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH);
    }
}

