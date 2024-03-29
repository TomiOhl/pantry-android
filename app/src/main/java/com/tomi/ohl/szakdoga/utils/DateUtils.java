package com.tomi.ohl.szakdoga.utils;

import android.content.Context;

import com.tomi.ohl.szakdoga.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Dátumokat formázó segédosztály.
 */
public class DateUtils {
    /**
     * Konvertálás unix időbélyegről dátum és időre.
     * @param ctx kontextus.
     * @param timestampInMillis unix időbélyeg milliszekundumokban.
     * @return a formázott dátum Stringként.
     */
    public static String convertToDateAndTime(Context ctx, long timestampInMillis) {
        return convert(ctx.getString(R.string.format_datetime), timestampInMillis);
    }

    /**
     * Konvertálás unix időbélyegről dátumra.
     * @param ctx kontextus.
     * @param timestampInMillis unix időbélyeg milliszekundumokban.
     * @return a formázott dátum Stringként.
     */
    public static String convertToDate(Context ctx, long timestampInMillis) {
        return convert(ctx.getString(R.string.format_date), timestampInMillis);
    }

    /**
     * Konvertálás unix időbélyegről a megadott formátumra.
     * @param format a formátum.
     * @param timestampInMillis unix időbélyeg milliszekundumokban.
     * @return a formázott dátum Stringként.
     */
    private static String convert(String format, long timestampInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestampInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(c.getTime());
    }
}
