package lk.ijse.chatapplication.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh : mm a");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public static void main(String[] args) {
        updatingDateTime();
    }

    private static void updatingDateTime() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {}, 0, 1, TimeUnit.SECONDS);
    }

    public static String getFormattedTime() {
        return LocalTime.now().format(timeFormatter);
    }

    public static String getFormattedDate() {
        return LocalDate.now().format(dateTimeFormatter);
    }
}
