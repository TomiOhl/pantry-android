package com.tomi.ohl.szakdoga.adapters.viewholders;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.tomi.ohl.szakdoga.R;

public class ShoppingListViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout shoppingItem;
    private MaterialCheckBox shoppingCheck;
    private EditText shoppingName;

    public ShoppingListViewHolder(@NonNull View itemView) {
        super(itemView);
        shoppingItem = itemView.findViewById(R.id.shoppingItem);
        shoppingCheck = itemView.findViewById(R.id.shoppingCheck);
        shoppingName = itemView.findViewById(R.id.shoppingName);
        // Raw input type: hogy maradhasson az action done több sor mellett is
        shoppingName.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    }

    public LinearLayout getShoppingItem() {
        return shoppingItem;
    }

    public MaterialCheckBox getShoppingCheck() {
        return shoppingCheck;
    }

    public EditText getShoppingName() {
        return shoppingName;
    }
}
