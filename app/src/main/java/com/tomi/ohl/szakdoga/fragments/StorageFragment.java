package com.tomi.ohl.szakdoga.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StoragePagerAdapter;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.views.AddStorageItemBottomSheet;

/**
 * A fő fragment, ami otthont ad a tárolók megjelenítéséért felelős felületnek.
 */
public class StorageFragment extends Fragment {

    private Spinner sortSpinner;
    private StorageItemsFragment fridgeListFragment;
    private StorageItemsFragment pantryListFragment;
    private SharedPreferences sharedPreferences;

    public StorageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A fragment layoutja
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        // Háztartás megjelenítése, csak álló módban
        TextView familyTextView = layout.findViewById(R.id.textCurrentFamily);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            familyTextView.setText(String.format(getString(R.string.family), FamilyController.getInstance().getCurrentFamily()));
        else
            familyTextView.setVisibility(View.GONE);

        // Sharedpreferences inicializálása
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        // Rendezés spinner inicializálása
        sortSpinner = layout.findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> sortSpinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.spinner_sort, android.R.layout.simple_spinner_item);
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(onSortSpinnerClick(layout));

        // Hozzáadás FAB
        FloatingActionButton addFab = layout.findViewById(R.id.fabAdd);
        addFab.setOnClickListener(view -> {
            requireActivity().getIntent().putExtra("itemId", "");
            requireActivity().getIntent().putExtra("storageItem", new StorageItem());
            AddStorageItemBottomSheet addItemSheet = new AddStorageItemBottomSheet();
            addItemSheet.show(getChildFragmentManager(), AddStorageItemBottomSheet.class.getSimpleName());
        });

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Hűtőszekrény és kamra rendezési sorrendjének beállítása, ami egyben kilistázza a megfelelő sorrendben
        int sortIndex = sharedPreferences.getInt("sortindex", 0);
        sortSpinner.setSelection(sortIndex);
    }

    /**
     * A rendezés egyik elemére kattintva lefutó metódus.
     * @param layout a befoglaló felület.
     * @return választásra való listener.
     */
    AdapterView.OnItemSelectedListener onSortSpinnerClick(View layout) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortBy = "name";
                switch (position) {
                    case 0: sortBy = "name";
                        break;
                    case 1: sortBy = "shelf";
                        break;
                    case 2: sortBy = "date";
                }
                StorageController.getInstance().setSortStoragesBy(sortBy);
                createOrLoadStorageItems(layout);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("sortindex", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    /**
     * Tárolólisták fragmentjeinek létrehozása vagy frissítése.
     * @param layout a befoglaló felület.
     */
    private void createOrLoadStorageItems(View layout) {
        if (fridgeListFragment == null || pantryListFragment == null ||
                fridgeListFragment.getView() == null || pantryListFragment.getView() == null) {
            createTabsWithViewPager(layout);
        } else {
            fridgeListFragment.updateContent();
            pantryListFragment.updateContent();
        }
    }

    /**
     * Tabek létrehozása és tárolólisták rárakása.
     * @param layout a befoglaló felület.
     */
    private void createTabsWithViewPager(View layout) {
        fridgeListFragment = StorageItemsFragment.newInstance(0);
        pantryListFragment = StorageItemsFragment.newInstance(1);

        // Adapter létrehozása, ami meghatározza, mikor melyik listát kell betölteni
        StoragePagerAdapter storagePagerAdapter = new StoragePagerAdapter(getChildFragmentManager(), requireActivity(), fridgeListFragment, pantryListFragment);

        // ViewPager a fenti adapterrel
        ViewPager storagePager = layout.findViewById(R.id.storageViewPager);
        storagePager.setAdapter(storagePagerAdapter);

        // Tárhelyválasztó tabek
        TabLayout tabLayout = layout.findViewById(R.id.tabStorageChooser);
        tabLayout.setupWithViewPager(storagePager);
    }

}