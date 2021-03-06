package com.tomi.ohl.szakdoga.utils;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.views.AddStorageItemBottomSheet;

import java.util.Locale;

public class DialogUtils {

    // Tárolt elemekre kattintva felugró ablak
    // TODO: egyszerűbb módja egy kivételének, véletlen törlés kivédése
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
            .setPositiveButton(R.string.modify, (dialogInterface, i) -> {
                Fragment parent = ((FragmentActivity) ctx).getSupportFragmentManager().findFragmentByTag("StorageFragment");
                if (parent != null) {
                    parent.requireActivity().getIntent().putExtra("itemId", itemId);
                    parent.requireActivity().getIntent().putExtra("storageItem", item);
                    AddStorageItemBottomSheet addItemSheet = new AddStorageItemBottomSheet();
                    addItemSheet.show(parent.getChildFragmentManager(), AddStorageItemBottomSheet.class.getSimpleName());
                }
            })
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
