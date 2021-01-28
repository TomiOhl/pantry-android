package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.ArrayList;

public class StorageListAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<StorageItem> items;

    public StorageListAdapter(Context c, ArrayList<StorageItem> list) {
        ctx = c;
        items = list;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
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
            row = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        } else {
            row = convertView;
        }
        StorageItem storageItem = items.get(i);
        TextView name = row.findViewById(android.R.id.text1);
        name.setText(storageItem.getName());
        return row;
    }
}
