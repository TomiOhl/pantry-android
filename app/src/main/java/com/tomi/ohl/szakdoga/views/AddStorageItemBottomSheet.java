package com.tomi.ohl.szakdoga.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StorageChooserMenuAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.utils.InputUtils;

public class AddStorageItemBottomSheet extends BottomSheetDialogFragment {
    private EditText nameEditText;
    private TextInputLayout nameEditTextContainer;
    private EditText countEditText;
    private TextInputLayout countEditTextContainer;
    private AutoCompleteTextView menuStorageChooser;
    private TextInputLayout menuStorageChooserContainer;
    private EditText shelfEditText;
    private TextInputLayout shelfEditTextContainer;
    private String itemId;
    private StorageItem currentItem;

    StorageChooserMenuAdapter storageTypeAdapter;

    public AddStorageItemBottomSheet() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedBottomSheetStyle);
        Intent i = requireActivity().getIntent();
        itemId = i.getStringExtra("itemId");
        currentItem = (StorageItem) i.getSerializableExtra("storageItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A sheet layoutja
        View layout = inflater.inflate(R.layout.bottom_sheet_add, container, false);

        // Beviteli mezők
        nameEditText = layout.findViewById(R.id.editTextAddItemName);
        nameEditTextContainer = layout.findViewById(R.id.textInputLayoutAddItemName);
        countEditText = layout.findViewById(R.id.editTextAddItemVolume);
        countEditTextContainer = layout.findViewById(R.id.textInputLayoutAddItemVolume);
        menuStorageChooser = layout.findViewById(R.id.menuStoragechooser);
        menuStorageChooserContainer = layout.findViewById(R.id.menuStoragechooserContainer);
        shelfEditText = layout.findViewById(R.id.editTextAddItemShelf);
        shelfEditTextContainer = layout.findViewById(R.id.textInputLayoutAddItemShelf);
        setInputTextWatchers();

        Button saveAddButton = layout.findViewById(R.id.btnSaveAdd);
        Button cancelAddButton = layout.findViewById(R.id.btnCancelAdd);

        // Tárhelyválasztó menü
        // Később majd akár a user által szerkeszthetővé lehetne tenni a tömböt
        String[] storageTypes = {getString(R.string.fridge), getString(R.string.pantry)};
        storageTypeAdapter = new StorageChooserMenuAdapter(requireContext(), R.layout.menu_add_choose_storage, storageTypes);
        menuStorageChooser.setAdapter(storageTypeAdapter);

        // Gombok eseménykezelői
        cancelAddButton.setOnClickListener(view -> dismiss());
        if (itemId.isEmpty()) {
            // Új hozzáadás, ilyenkor menteni kell elemet az adatbázisba
            saveAddButton.setOnClickListener(view -> {
                if (invalidInputs()) return;
                Snackbar.make(requireActivity().findViewById(R.id.storageLayout), R.string.saving, Snackbar.LENGTH_SHORT).show();
                StorageItem newItem = new StorageItem(
                        nameEditText.getText().toString(),
                        Integer.parseInt(String.valueOf(countEditText.getText())),
                        storageTypeAdapter.getIdOfItem(menuStorageChooser.getText().toString()),
                        Integer.parseInt(String.valueOf(shelfEditText.getText())),
                        System.currentTimeMillis()
                );
                StorageController.getInstance().insertStorageItem(newItem);
                dismiss();
            });
        } else {
            // Elem szerkesztése, ilyenkor frissíteni kell a meglévő elemet, az inputokat pedig kitölteni a mostaniakkal
            nameEditText.setText(currentItem.getName());
            countEditText.setText(String.valueOf(currentItem.getCount()));
            menuStorageChooserContainer.setVisibility(View.GONE);
            shelfEditText.setText(String.valueOf(currentItem.getShelf()));
            saveAddButton.setOnClickListener(view -> {
                if (invalidInputs()) return;
                Snackbar.make(requireActivity().findViewById(R.id.storageLayout), R.string.saving, Snackbar.LENGTH_SHORT).show();
                StorageController.getInstance().editStorageItem(
                        itemId,
                        Integer.parseInt(String.valueOf(countEditText.getText())),
                        String.valueOf(nameEditText.getText()),
                        Integer.parseInt(String.valueOf(shelfEditText.getText()))
                );
                dismiss();
            });
        }

        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return layout;
    }

    private boolean invalidInputs() {
        // Üres inputok kivédése
        if (nameEditText.getText().toString().trim().isEmpty()) {
            nameEditTextContainer.setError(getString(R.string.require_not_empty));
            nameEditText.requestFocus();
            return true;
        }
        if (countEditText.getText().toString().trim().isEmpty()) {
            countEditTextContainer.setError(getString(R.string.require_not_empty));
            countEditText.requestFocus();
            return true;
        }
        if (menuStorageChooserContainer.getVisibility() == View.VISIBLE
                && menuStorageChooser.getText().toString().trim().isEmpty()) {
            menuStorageChooserContainer.setError(getString(R.string.require_not_empty));
            menuStorageChooser.requestFocus();
            return true;
        }
        if (shelfEditText.getText().toString().trim().isEmpty()) {
            shelfEditTextContainer.setError(getString(R.string.require_not_empty));
            shelfEditText.requestFocus();
            return true;
        }
        return false;
    }

    // Beviteli mezőkről hiba eltüntetése, ha írni kezdünk beléjük
    private void setInputTextWatchers() {
        InputUtils.clearInputLayoutErrors(nameEditTextContainer, nameEditText);
        InputUtils.clearInputLayoutErrors(countEditTextContainer, countEditText);
        InputUtils.clearInputLayoutErrors(menuStorageChooserContainer, menuStorageChooser);
        InputUtils.clearInputLayoutErrors(shelfEditTextContainer, shelfEditText);
    }
}
