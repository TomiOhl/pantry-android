package com.tomi.ohl.szakdoga.utils;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;

public class DialogUtils {

    // Tárolóból való törlés megakadályozása
    public static void showConfirmDeleteStorageItemDialog(Activity activity, String itemId, String itemName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
            .setMessage(activity.getString(R.string.confirm_delete_item))
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.delete, (dialogInterface, i) ->
                    StorageController.getInstance().deleteStorageItem(itemId, itemName))
            .show();
    }

    // Egy kis hint megjelenítése egy-egy feature első használatához
    public static void showFirstUseDialog(Activity activity, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
        builder
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton(R.string.got_it, (dialogInterface, i) -> dialogInterface.cancel())
            .show();
    }

    // Üzenet akaratlan elvetését megakadályozó ablak
    public static void showConfirmDiscardMessageDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
            .setMessage(activity.getString(R.string.confirm_discard_message))
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.discard, (dialogInterface, i) -> activity.finish())
            .show();
    }

}
