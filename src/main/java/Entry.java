package com.cwacos;
public class Entry {

    double open, close, low, high;
    int volume;

    public Entry(){
    }

    public Entry (double _open, double _close, double _low, double _high, int _volume){
        this.open = _open;
        this.close = _close;
        this.low = _low;
        this.high = _high;
        this.volume = _volume;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "open=" + open +
                ", close=" + close +
                ", low=" + low +
                ", high=" + high +
                ", volume=" + volume +
                '}';
    }
}
