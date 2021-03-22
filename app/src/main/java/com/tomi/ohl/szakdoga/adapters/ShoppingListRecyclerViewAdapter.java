package com.tomi.ohl.szakdoga.adapters;

import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

public class ShoppingListRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListViewHolder> {
    private LinkedHashMap<String, ShoppingListItem> items;
    private ArrayList<String> keys;
    private String lastEditedItemKey;
    private int lastDeletedItemPosition;

    public ShoppingListRecyclerViewAdapter(LinkedHashMap<String, ShoppingListItem> list) {
        items = list;
        keys = new ArrayList<>(items.keySet());
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_list_item, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        String key = keys.get(position);
        boolean isChecked = Objects.requireNonNull(items.get(key)).isChecked();
        String name = Objects.requireNonNull(items.get(key)).getName();
        EditText shoppingNameEdit = holder.getShoppingName();
        shoppingNameEdit.setText(name);
        CheckBox shoppingNameCheck = holder.getShoppingCheck();
        shoppingNameCheck.setChecked(isChecked);
        if (isChecked)
            shoppingNameEdit.setPaintFlags(shoppingNameEdit.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            shoppingNameEdit.setPaintFlags(shoppingNameEdit.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        shoppingNameCheck.setOnCheckedChangeListener((buttonView, isCheckChecked) -> {
            int currentPos = holder.getAdapterPosition();
            String currentKey = keys.get(currentPos);
            StorageController.getInstance().editShoppingListItem(
                    currentKey,
                    new ShoppingListItem(null, isCheckChecked)
            );
        });
        // Módosított szöveg mentése akkor, amikor a user átkattint
        shoppingNameEdit.setOnFocusChangeListener((view, hasFocus) -> {
            EditText editText = (EditText) view;
            if (hasFocus) {
                lastEditedItemKey = keys.get(holder.getAdapterPosition());
                editText.setSelection(editText.getText().length());
            }
            else
                StorageController.getInstance().editShoppingListItem(
                        lastEditedItemKey,
                        new ShoppingListItem(editText.getText().toString(), null)
                );
        });
        // Az utolsó elem kap egy nagy paddinget a FAB miatt
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (position == getItemCount() -1) {
            DisplayMetrics displayMetrics = holder.getShoppingItem().getResources().getDisplayMetrics();
            params.bottomMargin = (int) ((88 * displayMetrics.density) + 0.5);
        }
        else
            params.bottomMargin = 0;
        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public String getKeyAtPosition(int position) {
        return keys.get(position);
    }

    public void setLastDeletedItemPosition(int position) {
        lastDeletedItemPosition = position;
    }

    // Note: a lastDeletedItemPositionös sorok és a notifyItemRangeChanged helyett lehetne
    // egy sima notifyDataSetChanged hívás is,
    // azonban úgy szerkesztés után máshova kattintva mindig az első EditText kapná a fókuszt.
    public void updateKeys(Set<String> newKeys) {
        notifyItemRemoved(lastDeletedItemPosition);
        keys.clear();
        keys.addAll(new ArrayList<>(newKeys));
        notifyItemRangeChanged(0, getItemCount());
        lastDeletedItemPosition = getItemCount();
    }
}