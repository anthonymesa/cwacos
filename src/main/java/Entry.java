
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public String out() {
        DateFormat formatter;

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        return  open + " " + close + " " + low + " " + high + " " + volume + " " + formatter.format(dateTime);
    }

    @Override
    public int compareTo(Entry _entry) {
        return this.dateTime.compareTo(_entry.dateTime);
    }
}
