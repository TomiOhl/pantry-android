package com.tomi.ohl.szakdoga.utils;

import android.content.Context;

import com.tomi.ohl.szakdoga.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    public static String convertToDateAndTime(Context ctx, long timestampInMillis) {
        return convert(ctx.getString(R.string.format_datetime), timestampInMillis);
    }

    public static String convertToDate(Context ctx, long timestampInMillis) {
        return convert(ctx.getString(R.string.format_date), timestampInMillis);
    }

    private static String convert(String format, long timestampInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestampInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(c.getTime());
    }
}
