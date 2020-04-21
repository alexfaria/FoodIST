package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OpeningHours {

    public static final SimpleDateFormat df;

    static {
        df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final Date open;
    private final Date close;

    public OpeningHours(String openingHours) throws ParseException {
        String[] split = openingHours.split("-");
        this.open = df.parse(split[0]);
        this.close = df.parse(split[1]);
    }

    public Date getOpen() {
        return open;
    }

    public Date getClose() {
        return close;
    }

    @Override
    public String toString() {
        return df.format(open) + "-" + df.format(close);
    }
}
