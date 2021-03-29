package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.utils.DateUtils;
import com.tomi.ohl.szakdoga.views.StorageItemDetailsBottomSheet;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            row = inflater.inflate(R.layout.storage_list_item, parent, false);
        } else {
            row = convertView;
        }
        StorageItem storageItem = (StorageItem) getItem(position);
        TextView name = row.findViewById(R.id.listTitle);
        name.setText(storageItem.getName());
        TextView shelf = row.findViewById(R.id.listShelf);
        shelf.setText(String.format(Locale.getDefault(),
                "%d%s", storageItem.getShelf(), ctx.getString(R.string.nth_shelf)
        ));
        TextView date = row.findViewById(R.id.listDate);
        date.setText(DateUtils.convertToDate(storageItem.getDate()));
        TextView count = row.findViewById(R.id.listCount);
        count.setText(String.format("%s%s",storageItem.getCount(), 'x'));
        row.setOnClickListener(view -> {
                FragmentManager fm = ((FragmentActivity) ctx).getSupportFragmentManager();
                Fragment parentFragment = fm.findFragmentByTag("StorageFragment") == null ?
                        fm.findFragmentByTag("SearchResultFragment") : fm.findFragmentByTag("StorageFragment");
                if (parentFragment != null) {
                    parentFragment.requireActivity().getIntent().putExtra("itemId", keys.get(position));
                    parentFragment.requireActivity().getIntent().putExtra("storageItem", (StorageItem) getItem(position));
                    StorageItemDetailsBottomSheet itemDetailsSheet = new StorageItemDetailsBottomSheet();
                    itemDetailsSheet.show(parentFragment.getChildFragmentManager(), StorageItemDetailsBottomSheet.class.getSimpleName());
                }
            }
        );
        return row;
    }

    public void updateKeys(Set<String> newKeys) {
        keys.clear();
        keys.addAll(new ArrayList<>(newKeys));
        notifyDataSetChanged();
    }
}
