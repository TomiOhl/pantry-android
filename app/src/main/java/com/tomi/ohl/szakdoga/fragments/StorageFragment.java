package com.tomi.ohl.szakdoga.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StorageListAdapter;
import com.tomi.ohl.szakdoga.adapters.StoragePagerAdapter;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.views.AddStorageItemBottomSheet;
import com.tomi.ohl.szakdoga.views.PaddedListFragment;

import java.util.LinkedHashMap;

public class StorageFragment extends Fragment {

    private Spinner sortSpinner;
    private PaddedListFragment fridgeListFragment;
    private PaddedListFragment pantryListFragment;
    SharedPreferences sharedPreferences;

    public StorageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tárolók listafragmentjeinek létrehozása
        fridgeListFragment = new PaddedListFragment(getString(R.string.storage_empty));
        pantryListFragment = new PaddedListFragment(getString(R.string.storage_empty));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A fragment layoutja
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        // Háztartás megjelenítése
        TextView familyTextView = layout.findViewById(R.id.textCurrentFamily);
        familyTextView.setText(String.format(getString(R.string.family), FamilyController.getInstance().getCurrentFamily()));

        // Sharedpreferences inicializálása
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        // Rendezés spinner inicializálása
        sortSpinner = layout.findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> sortSpinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.spinner_sort, android.R.layout.simple_spinner_item);
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(onSortSpinnerClick());

        // Adapter létrehozása, ami meghatározza, mikor melyik listát kell betölteni
        StoragePagerAdapter storagePagerAdapter = new StoragePagerAdapter(getChildFragmentManager(), this.getContext(), fridgeListFragment, pantryListFragment);

        // ViewPager a fenti adapterrel
        ViewPager storagePager = layout.findViewById(R.id.storageViewPager);
        storagePager.setAdapter(storagePagerAdapter);

        // Tárhelyválasztó tabek
        TabLayout tabLayout = layout.findViewById(R.id.tabStorageChooser);
        tabLayout.setupWithViewPager(storagePager);

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

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity)requireActivity()).dbListeners.size() < 1) {
            String sortBy = sharedPreferences.getString("sortname", "name");
            loadStorageContents(fridgeListFragment, 0, sortBy);
            loadStorageContents(pantryListFragment, 1, sortBy);
        }
    }

    // Az adott tároló tartalmának figyelése, listázás és onClick beállítása/frissítése
    private void loadStorageContents(PaddedListFragment listFragment, int storage, String sortBy) {
        LinkedHashMap<String, StorageItem> itemsMap = new LinkedHashMap<>();
        StorageListAdapter listAdapter = new StorageListAdapter(this.requireContext(), itemsMap);
        listFragment.setListAdapter(listAdapter);
        ((MainActivity)requireActivity()).dbListeners.add(StorageController.getInstance().getStorageItems(storage, sortBy).addSnapshotListener(
                (value, error) -> {
                    assert value != null;
                    itemsMap.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String id = doc.getId();
                        StorageItem item = doc.toObject(StorageItem.class);
                        itemsMap.put(id, item);
                    }
                    listAdapter.updateKeys(itemsMap.keySet());
                }
        ));
    }

    AdapterView.OnItemSelectedListener onSortSpinnerClick() {
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
                ((MainActivity)requireActivity()).removeDbListeners();
                loadStorageContents(fridgeListFragment, 0, sortBy);
                loadStorageContents(pantryListFragment, 1, sortBy);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("sortindex", position);
                editor.putString("sortname", sortBy);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

}