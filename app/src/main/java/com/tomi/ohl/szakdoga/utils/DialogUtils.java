package com.tomi.ohl.szakdoga.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.Locale;

public class DialogUtils {

    // TODO: gombok eseménykezelői
    public static void showItemDetailsDialog(Context ctx, String itemId, StorageItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
            .setTitle(ctx.getString(R.string.details))
            .setMessage(String.format(Locale.getDefault(),
                    "%s: %s\n%s: %d\n%s: %d%s\n%s: %s",
                    ctx.getString(R.string.name), item.getName(),
                    ctx.getString(R.string.volume), item.getCount(),
                    ctx.getString(R.string.shelf), item.getShelf(), ctx.getString(R.string.nth_shelf),
                    ctx.getString(R.string.date), DateUtils.convertToDateAndTime(item.getDate())
            ))
            .setCancelable(true)
            .setNegativeButton(R.string.delete, (dialogInterface, i) ->
                    StorageController.getInstance().deleteStorageItem(itemId))
            .setNeutralButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.modify, (dialogInterface, i) -> dialogInterface.cancel())
            .show();
    }

}
