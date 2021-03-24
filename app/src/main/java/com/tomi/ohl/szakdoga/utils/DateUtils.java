package com.tomi.ohl.szakdoga.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    public static String convertToDateAndTime(long timestampInMillis) {
        return convert("yyyy.M.d. HH:mm:ss", timestampInMillis);
    }

    public static String convertToDate(long timestampInMillis) {
        return convert("yyyy.M.d.", timestampInMillis);
    }

    private static String convert(String format, long timestampInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestampInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(c.getTime());
    }
}
