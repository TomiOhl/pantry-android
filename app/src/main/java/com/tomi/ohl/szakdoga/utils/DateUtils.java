package com.tomi.ohl.szakdoga.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    public static String convertToDateAndTime(long timestampInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestampInMillis);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.M.d. HH:mm:ss", Locale.getDefault());
        return format1.format(c.getTime());
    }
}
