package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OpeningHours {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime open;
    private final LocalTime close;

    public OpeningHours(String openingHours) {
        String[] time = openingHours.split("-");
        open = LocalTime.parse(time[0], formatter);
        close = LocalTime.parse(time[1], formatter);
    }

    public LocalTime getOpen() {
        return open;
    }

    public LocalTime getClose() {
        return close;
    }

    public boolean isAvailable(LocalTime current) {
        return open.isBefore(current) && close.isAfter(current);
    }

    @Override
    public String toString() {
        return open.format(formatter) + "-" + close.format(formatter);
    }
}
