package com.tomi.ohl.szakdoga.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DialogUtils {

    // TODO: gombok eseménykezelői
    public static void showItemDetailsDialog(Context ctx, StorageItem item) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(item.getDate());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.M.d. HH:mm:ss", Locale.getDefault());
        String itemDate = format1.format(c.getTime());
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
            .setTitle(ctx.getString(R.string.details))
            .setMessage(String.format(Locale.getDefault(),
                    "%s: %s\n%s: %d\n%s: %d%s\n%s: %s",
                    ctx.getString(R.string.name), item.getName(),
                    ctx.getString(R.string.volume), item.getCount(),
                    ctx.getString(R.string.shelf), item.getShelf(), ctx.getString(R.string.nthShelf),
                    ctx.getString(R.string.date), itemDate
            ))
            .setCancelable(true)
            .setNegativeButton(R.string.delete, (dialogInterface, i) -> dialogInterface.cancel())
            .setNeutralButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.modify, (dialogInterface, id) -> dialogInterface.cancel())
            .show();
    }

}
