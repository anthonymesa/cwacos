public class Entry {

    double open, close, low, high;
    int volume;
    String dateTime;

    public Entry(){
    }

    public Entry (double _open, double _close, double _low, double _high, int _volume, String _dateTime){
        this.open = _open;
        this.close = _close;
        this.low = _low;
        this.high = _high;
        this.volume = _volume;
        this.dateTime = _dateTime;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "open=" + open +
                ", close=" + close +
                ", low=" + low +
                ", high=" + high +
                ", volume=" + volume +
                ", dateTime=" + dateTime +
                '}';
    }
}
