package com.tomi.ohl.szakdoga.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.utils.InputUtils;

public class AddShoppingListItemBottomSheet extends BottomSheetDialogFragment {

    public AddShoppingListItemBottomSheet() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedBottomSheetStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.bottom_sheet_add_shopping_list, container, false);

        Button cancelBtn = layout.findViewById(R.id.btnCancelAddShoppingList);
        cancelBtn.setOnClickListener(view -> dismiss());
        Button addBtn = layout.findViewById(R.id.btnSaveAddShoppingList);
        EditText newItemEditText = layout.findViewById(R.id.editTextAddShoppingListName);
        TextInputLayout newItemEditTextContainer = layout.findViewById(R.id.textInputLayoutAddShoppingListName);
        InputUtils.clearInputLayoutErrors(newItemEditTextContainer, newItemEditText);
        addBtn.setOnClickListener(view -> {
            String newItemName = newItemEditText.getText().toString().trim();
            if (!newItemName.isEmpty()) {
                StorageController.getInstance().insertShoppingListItem(
                        new ShoppingListItem(newItemName, false)
                );
                dismiss();
            }
            else
                newItemEditTextContainer.setError(getString(R.string.require_not_empty));

        });

        return layout;
    }
}
