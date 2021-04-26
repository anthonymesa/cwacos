package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: Entry defines a single financial data element over a single interval of time. Because this
 *      is used for all financial data manipulation and data storage, all data types (i.e. Stock, Crypto, etc.)
 *      must use an ArrayList of Entry type to store their data.
 * 
 * Contributing Authors:
 *      Michael Leonard
 *      Anthony Mesa
 */

import java.text.DateFormat;
import java.util.Date;

public class Entry implements Comparable<Entry> {

    private DateFormat formatter = CwacosDateFormat.getDateFormat();
    private double open, close, low, high;
    private int volume;
    private Date dateTime;

    public Entry() {
        this(0.0, 0.0, 0.0, 0.0, 0, new Date());
    }

    public Entry(double _open, double _close, double _low, double _high, int _volume, Date _dateTime) {
        this.open = _open;
        this.close = _close;
        this.low = _low;
        this.high = _high;
        this.volume = _volume;
        this.dateTime = _dateTime;
    }

    @Override
    public String toString() {
        return open + " " + close + " " + low + " " + high + " " + volume + " " + formatter.format(dateTime);
    }

    @Override
    public int compareTo(Entry _entry) {
        return this.dateTime.compareTo(_entry.dateTime);
    }

    //==============================================================================
    // Getters
    //==============================================================================

    public DateFormat getFormatter() {
        return formatter;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }

    public int getVolume() {
        return volume;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getOpenString() {
        return Double.toString(open);
    }

    public String getCloseString() {
        return Double.toString(close);
    }

    public String getLowString() {
        return Double.toString(low);
    }

    public String getHighString() {
        return Double.toString(high);
    }

    public String getVolumeString() {
        return Integer.toString(volume);
    }

    public String getDateTimeString() { return formatter.format(dateTime);}

    //==============================================================================
    // Setters
    //==============================================================================

    public void setFormatter(DateFormat formatter) {
        this.formatter = formatter;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}


