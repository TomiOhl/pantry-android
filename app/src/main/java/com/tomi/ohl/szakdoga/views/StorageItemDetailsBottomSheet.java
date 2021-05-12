package com.tomi.ohl.szakdoga.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.utils.DateUtils;
import com.tomi.ohl.szakdoga.utils.DialogUtils;

import java.util.Locale;

/**
 * Tárolt elemekre kattintva felugró felület.
 */
public class StorageItemDetailsBottomSheet extends BottomSheetDialogFragment {
    private String itemId;
    private StorageItem item;
    private TextView countText;

    public StorageItemDetailsBottomSheet() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedBottomSheetStyle);
        Intent i = requireActivity().getIntent();
        itemId = i.getStringExtra("itemId");
        item = (StorageItem) i.getSerializableExtra("storageItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A sheet layoutja
        View layout = inflater.inflate(R.layout.bottom_sheet_storage_item_details, container, false);

        // Szövegek
        TextView detailsText = layout.findViewById(R.id.textStorageItemDetails);
        detailsText.setText(String.format(Locale.getDefault(),
                "%s: %s\n%s: %d %s (%s)\n%s: %s",
                getString(R.string.name), item.getName(),
                getString(R.string.shelf), item.getShelf(), getString(R.string.nth_shelf), getStorageName(item.getLocation()),
                getString(R.string.date), DateUtils.convertToDateAndTime(requireContext(), item.getDate())
        ));

        countText = layout.findViewById(R.id.textItemCount);
        countText.setText(String.format(Locale.getDefault(),
                "%s: %d",
                getString(R.string.volume), item.getCount()
        ));

        // Gombok
        Button lessBtn = layout.findViewById(R.id.btnLessItems);
        lessBtn.setOnClickListener(view -> setItemCount(false));
        Button moreBtn = layout.findViewById(R.id.btnMoreItems);
        moreBtn.setOnClickListener(view -> setItemCount(true));
        Button editBtn = layout.findViewById(R.id.btnEditStorageItem);
        editBtn.setOnClickListener(view -> {
            AddStorageItemBottomSheet addItemSheet = new AddStorageItemBottomSheet();
            addItemSheet.show(getParentFragmentManager(), AddStorageItemBottomSheet.class.getSimpleName());
            dismiss();
        });
        Button deleteBtn = layout.findViewById(R.id.btnDeleteStorageItem);
        deleteBtn.setOnClickListener(view -> {
            DialogUtils.showConfirmDeleteStorageItemDialog(requireActivity(), itemId, item.getName());
            dismiss();
        });
        Button cancelBtn = layout.findViewById(R.id.btnCancelStorageItemDetails);
        cancelBtn.setOnClickListener(view -> dismiss());

        return layout;
    }

    /**
     * Gyors mennyiségállítás kezelése.
     * @param isMore több-e az új érték, mint az előző.
     */
    private void setItemCount(boolean isMore) {
        int newCount = item.getCount();
        if (isMore)
            newCount += 1;
        else if (newCount > 0)
            newCount -= 1;
        StorageController.getInstance().editStorageItem(itemId, newCount, item.getName(), item.getShelf());
        item.setCount(newCount);
        countText.setText(String.format(Locale.getDefault(),
                "%s: %d",
                getString(R.string.volume), newCount
        ));
    }

    /**
     * Tároló nevének lekérése id alapján.
     * Ez persze nem így fog kinézni akkor, ha dinamikus lenne valamikor.
     * @param id a tároló id-je.
     * @return a tároló megjelenítendő neve.
     */
    private String getStorageName(int id) {
        String name = id == 0 ? getString(R.string.fridge) : getString(R.string.pantry);
        return name.toLowerCase();
    }
}
