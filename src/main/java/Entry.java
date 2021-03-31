import java.util.Date;

public class Entry implements Comparable<Entry>{

    double open, close, low, high;
    int volume;
    Date dateTime;

    public Entry(){
    }

    public Entry (double _open, double _close, double _low, double _high, int _volume, Date _dateTime){
        this.open = _open;
        this.close = _close;
        this.low = _low;
        this.high = _high;
        this.volume = _volume;
        this.dateTime = _dateTime;
    }

    @Override
    public String toString() {
        return  '{' +
                "open=" + open +
                ", close=" + close +
                ", low=" + low +
                ", high=" + high +
                ", volume=" + volume +
                ", timeDate=" + dateTime +
                '}';
    }

    @Override
    public int compareTo(Entry _entry) {
        return this.dateTime.compareTo(_entry.dateTime);
    }
}
