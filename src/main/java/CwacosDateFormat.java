import java.text.SimpleDateFormat;
import java.util.Locale;

public class CwacosDateFormat {
    public static  SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);
    }
}
