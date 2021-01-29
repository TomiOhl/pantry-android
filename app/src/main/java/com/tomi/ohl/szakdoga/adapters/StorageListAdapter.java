package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.utils.DialogUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

public class StorageListAdapter extends BaseAdapter {
    Context ctx;
    LinkedHashMap<String, StorageItem> items;
    ArrayList<String> keys;

    public StorageListAdapter(Context c, LinkedHashMap<String, StorageItem> list) {
        ctx = c;
        items = list;
        keys = new ArrayList<>(items.keySet());
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(keys.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View row;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            row = inflater.inflate(R.layout.storage_list_item, viewGroup, false);
        } else {
            row = convertView;
        }
        StorageItem storageItem = (StorageItem) getItem(i);
        TextView name = row.findViewById(R.id.listTitle);
        name.setText(storageItem.getName());
        TextView shelf = row.findViewById(R.id.listSuffix);
        shelf.setText(String.format(Locale.getDefault(),
                "%d%s", storageItem.getShelf(), ctx.getString(R.string.nth_shelf)
        ));
        row.setOnClickListener(view ->
            DialogUtils.showItemDetailsDialog(ctx, keys.get(i), (StorageItem) getItem(i))
        );
        return row;
    }

    public void updateKeys(Set<String> newKeys) {
        keys.clear();
        keys.addAll(new ArrayList<>(newKeys));
        notifyDataSetChanged();
    }
}
