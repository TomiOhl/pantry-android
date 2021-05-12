package com.tomi.ohl.szakdoga.utils;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;

/**
 * Dialógusokat megjelenítő segédosztály.
 */
public class DialogUtils {

    /**
     * Üzenet véletlen való törlésének megakadályozása.
     * @param ctx kontextus, ahol a Dialog megjelenítésre kerül.
     * @param itemId a törlendő üzenet id-je.
     */
    public static void showConfirmDeleteMessageDialog(Context ctx, String itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage(ctx.getString(R.string.confirm_delete_message))
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton(R.string.delete, (dialogInterface, i) ->
                        StorageController.getInstance().deleteMessage(itemId))
                .show();
    }

    /**
     * Tárolóból való  véletlen törlés megakadályozása.
     * @param activity az Activity, ahol a Dialog megjelenítésre kerül.
     * @param itemId a törlendő elem id-je.
     * @param itemName a törlendő elem neve.
     */
    public static void showConfirmDeleteStorageItemDialog(Activity activity, String itemId, String itemName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
            .setMessage(activity.getString(R.string.confirm_delete_item))
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.delete, (dialogInterface, i) ->
                    StorageController.getInstance().deleteStorageItem(itemId, itemName))
            .show();
    }

    /**
     * Üzenet akaratlan elvetését megakadályozó ablak.
     * @param activity az Activity, ahol a Dialog megjelenítésre kerül.
     */
    public static void showConfirmDiscardMessageDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setMessage(activity.getString(R.string.confirm_discard_message))
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton(R.string.discard, (dialogInterface, i) -> activity.finish())
                .show();
    }

    /**
     * Egy kis hint megjelenítése egy-egy feature első használatához.
     * @param activity az Activity, ahol a Dialog megjelenítésre kerül.
     * @param text a hint szövege.
     */
    public static void showFirstUseDialog(Activity activity, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
        builder
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton(R.string.got_it, (dialogInterface, i) -> dialogInterface.cancel())
            .show();
    }

}
