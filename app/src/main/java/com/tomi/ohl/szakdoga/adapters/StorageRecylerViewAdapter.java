package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.utils.DateUtils;
import com.tomi.ohl.szakdoga.views.StorageItemDetailsBottomSheet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

public class StorageRecylerViewAdapter extends RecyclerView.Adapter<StorageItemViewHolder> {
    LinkedHashMap<String, StorageItem> items;
    ArrayList<String> keys;

    public StorageRecylerViewAdapter(LinkedHashMap<String, StorageItem> list) {
        items = list;
        keys = new ArrayList<>(items.keySet());
    }

    @NonNull
    @Override
    public StorageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.storage_list_item, parent, false);
        return new StorageItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageItemViewHolder holder, int position) {
        Context ctx = holder.getContainer().getContext();
        StorageItem storageItem = getItem(position);
        holder.getTitle().setText(storageItem.getName());
        holder.getCount().setText(String.format("%s%s",storageItem.getCount(), 'x'));
        holder.getDate().setText(DateUtils.convertToDate(storageItem.getDate()));
        holder.getShelf().setText(String.format(Locale.getDefault(),
                "%d%s", storageItem.getShelf(), ctx.getString(R.string.nth_shelf)
        ));
        holder.getContainer().setOnClickListener(view -> displayDetails(ctx, position));
        holder.getContainer().setOnLongClickListener(view -> displayDetails(ctx, position));
        // Az utolsó elem kap egy nagy paddinget a FAB miatt
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (position == getItemCount() -1) {
            DisplayMetrics displayMetrics = holder.getContainer().getResources().getDisplayMetrics();
            params.bottomMargin = (int) ((88 * displayMetrics.density));
        }
        else
            params.bottomMargin = 0;
        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    private StorageItem getItem(int position) {
        String key = keys.get(position);
        return items.get(key);
    }

    // Részletek megjelenítése bottom sheeten
    private boolean displayDetails(Context ctx, int position) {
        FragmentManager fm = ((FragmentActivity) ctx).getSupportFragmentManager();
        Fragment parentFragment = fm.findFragmentByTag("StorageFragment") == null ?
                fm.findFragmentByTag("SearchResultFragment") : fm.findFragmentByTag("StorageFragment");
        if (parentFragment != null) {
            parentFragment.requireActivity().getIntent().putExtra("itemId", keys.get(position));
            parentFragment.requireActivity().getIntent().putExtra("storageItem", getItem(position));
            StorageItemDetailsBottomSheet itemDetailsSheet = new StorageItemDetailsBottomSheet();
            itemDetailsSheet.show(parentFragment.getChildFragmentManager(), StorageItemDetailsBottomSheet.class.getSimpleName());
            return true;
        }
        return false;
    }

    public void updateKeys(Set<String> newKeys) {
        keys.clear();
        keys.addAll(new ArrayList<>(newKeys));
        notifyDataSetChanged();
    }
}
