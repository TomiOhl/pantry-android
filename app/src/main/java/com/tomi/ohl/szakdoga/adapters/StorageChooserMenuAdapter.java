package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Tárolóválasztási menü adaptere.
 */
public class StorageChooserMenuAdapter extends ArrayAdapter<String> {
    List<String> items;

    public StorageChooserMenuAdapter(Context ctx, int layout, String[] storageItems) {
        super(ctx, layout, storageItems);
        items = Arrays.asList(storageItems);
    }

    /**
     * Egy tároló nevének visszaadása index alapján.
     * @param i a tároló indexe.
     * @return az elem az adott indexén a tömbnek.
     */
    @Override
    public String getItem(int i) {
        return items.get(i);
    }

    /**
     * Egy tároló indexének visszaadása a neve alapján.
     * @param item a tároló neve.
     * @return az adott elem indexe a tömbben.
     */
    public int getIdOfItem(String item) {
        if (items.contains(item))
            return items.indexOf(item);
        return 0;
    }
}
