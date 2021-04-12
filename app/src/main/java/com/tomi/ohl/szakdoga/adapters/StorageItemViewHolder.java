package com.tomi.ohl.szakdoga.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tomi.ohl.szakdoga.R;

public class StorageItemViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout container;
    private TextView title;
    private TextView count;
    private TextView date;
    private TextView shelf;

    public StorageItemViewHolder(@NonNull View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.storageListItem);
        title = itemView.findViewById(R.id.listTitle);
        count = itemView.findViewById(R.id.listCount);
        date = itemView.findViewById(R.id.listDate);
        shelf = itemView.findViewById(R.id.listShelf);
    }

    public LinearLayout getContainer() {
        return container;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getCount() {
        return count;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getShelf() {
        return shelf;
    }
}
