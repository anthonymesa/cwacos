/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Entry implements Comparable<Entry> {

    // You can now use CwacosDateFormat.getDateFormat(); to avoid reusing of code
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);

    private double open, close, low, high;
    private int volume;
    private Date dateTime;

    public Entry() {
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

    //---------------------------------GETTERS-----------------------------------//

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

    public double getCloseString() {
        return close;
    }

    public double getLowString() {
        return low;
    }

    public double getHighString() {
        return high;
    }

    public int getVolumeString() {
        return volume;
    }

    public Date getDateTimeString() {
        return dateTime;
    }

    //---------------------------------SETTERS-----------------------------------//


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


