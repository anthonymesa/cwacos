import java.util.Date;

public class Entry {

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
        return open + "," + close +
                ", low=" + low +
                ", high=" + high +
                ", volume=" + volume +
                ", timeDate=" + dateTime +
                '}';
    }
}
