package com.tomi.ohl.szakdoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StorageListAdapter;
import com.tomi.ohl.szakdoga.adapters.StoragePagerAdapter;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.utils.InputUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class StorageFragment extends Fragment {

    private TextView listTextView;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private EditText nameEditText;
    private TextInputLayout nameEditTextContainer;
    private EditText countEditText;
    private TextInputLayout countEditTextContainer;
    private AutoCompleteTextView menuStorageChooser;
    private TextInputLayout menuStorageChooserContainer;
    private EditText shelfEditText;
    private TextInputLayout shelfEditTextContainer;
    private Button saveAddButton;
    private Button cancelAddButton;
    private FirebaseUser user;
    private ListFragment fridgeListFragment;
    private ListFragment pantryListFragment;

    public StorageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        fridgeListFragment = new ListFragment();
        pantryListFragment = new ListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A fragment layoutja
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        TextView nameTextView = layout.findViewById(R.id.textProfileName);
        TextView emailTextView = layout.findViewById(R.id.textProfileEmail);
        TextView familyTextView = layout.findViewById(R.id.textProfileFamily);
        listTextView = layout.findViewById(R.id.textTestList);

        // Hozzááadás layout beviteli mezői
        nameEditText = layout.findViewById(R.id.editTextAddItemName);
        nameEditTextContainer = layout.findViewById(R.id.textInputLayoutAddItemName);
        countEditText = layout.findViewById(R.id.editTextAddItemVolume);
        countEditTextContainer = layout.findViewById(R.id.textInputLayoutAddItemVolume);
        menuStorageChooser = layout.findViewById(R.id.menuStoragechooser);
        menuStorageChooserContainer = layout.findViewById(R.id.menuStoragechooserContainer);
        shelfEditText = layout.findViewById(R.id.editTextAddItemShelf);
        shelfEditTextContainer = layout.findViewById(R.id.textInputLayoutAddItemShelf);
        setInputTextWatchers();

        Button addButton = layout.findViewById(R.id.btnTestAdd);
        saveAddButton = layout.findViewById(R.id.btnSaveAdd);
        cancelAddButton = layout.findViewById(R.id.btnCancelAdd);

        // Jelenítsük meg a bejelentkezett user adatait
        if (user != null) {
            nameTextView.setText(String.format("%s: %s", getString(R.string.display_name), user.getDisplayName()));
            emailTextView.setText(String.format("%s: %s", getString(R.string.email), user.getEmail()));
            familyTextView.setText(String.format("%s: %s", getString(R.string.family), FamilyController.getInstance().getCurrentFamily()));
            getTestList();
        }

        // Firestore read/write tesztelése
        addButton.setOnClickListener(view -> {
            // A user egyedi id-jével elnevezett dokumentumba beszúr egy adatot
            // A hozzáadás gomb többszöri megnyomása felülírja, de a kód működik több elemmel is
            StorageController.getInstance().insertTest();
            getTestList();
        });

        // Adapter létrehozása, ami meghatározza, mikor melyik listát kell betölteni
        StoragePagerAdapter storagePagerAdapter = new StoragePagerAdapter(getChildFragmentManager(), this.getContext(), fridgeListFragment, pantryListFragment);

        // ViewPager a fenti adapterrel
        ViewPager storagePager = layout.findViewById(R.id.storageViewPager);
        storagePager.setAdapter(storagePagerAdapter);

        // Tárhelyválasztó tabek
        TabLayout tabLayout = layout.findViewById(R.id.tabStorageChooser);
        tabLayout.setupWithViewPager(storagePager);

        // Hozzáadás layout
        View bottom_sheet = layout.findViewById(R.id.bottom_sheet_add);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Hozzáadás layout tárhelyválasztó menüje
        // TODO: Megcsinálni úgy, hogy rendesen használható legyen több nyelven is
        String[] storageTypes = {getString(R.string.fridge), getString(R.string.pantry)};
        ArrayAdapter<String> storageTypeAdapter = new ArrayAdapter<>(requireContext(), R.layout.menu_add_choose_storage, storageTypes);
        menuStorageChooser.setAdapter(storageTypeAdapter);

        // Hozzáadás FAB
        FloatingActionButton addFab = layout.findViewById(R.id.fabAdd);
        addFab.setOnClickListener(view -> openAddLayout(null, null));

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Hűtőszekrény és kamra kilistázása
        loadStorageContents(fridgeListFragment, getString(R.string.fridge));
        loadStorageContents(pantryListFragment, getString(R.string.pantry));
    }

    private void getTestList() {
        // Kilistázza az adott user adatait
        StorageController.getInstance().getTestInsert().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    HashMap<String, Object> userdata = (HashMap<String, Object>) document.getData();
                    StringBuilder userDataList = new StringBuilder();
                    assert userdata != null;
                    for (String elem : userdata.keySet())
                        userDataList.append(elem).append(" => ").append(userdata.get(elem)).append("\n");
                    listTextView.setText(userDataList);
                }
            } else {
                Toast.makeText(getContext(), "Az adatok lekérése sikertelen: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Az adott tároló tartalmának figyelése, listázás és onClick beállítása/frissítése
    private void loadStorageContents(ListFragment listFragment, String storage) {
        LinkedHashMap<String, StorageItem> itemsMap = new LinkedHashMap<>();
        StorageListAdapter listAdapter = new StorageListAdapter(this.requireContext(), itemsMap);
        listFragment.setListAdapter(listAdapter);
        ((MainActivity)requireActivity()).dbListeners.add(StorageController.getInstance().getStorageItems(storage).addSnapshotListener(
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

    // Ez hívódik meg, ha szükség van az elem hozzáadása bottom sheetre
    public void openAddLayout(String itemId, StorageItem item) {
        clearAddLayout();
        cancelAddButton.setOnClickListener(view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        if (itemId == null) {
            // Új hozzáadás, ilyenkor menteni kell elemet az adatbázisba
            saveAddButton.setOnClickListener(view -> {
                if (invalidInputs()) return;
                Snackbar.make(requireActivity().findViewById(R.id.storageLayout), R.string.saving, Snackbar.LENGTH_SHORT).show();
                StorageItem newItem = new StorageItem(
                        nameEditText.getText().toString(),
                        Integer.parseInt(String.valueOf(countEditText.getText())),
                        menuStorageChooser.getText().toString(),
                        Integer.parseInt(String.valueOf(shelfEditText.getText())),
                        System.currentTimeMillis()
                );
                StorageController.getInstance().insertStorageItem(newItem);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            });
        } else {
            // Elem szerkesztése, ilyenkor frissíteni kell a meglévő elemet, az inputokat pedig kitölteni a mostaniakkal
            nameEditText.setText(item.getName());
            countEditText.setText(String.valueOf(item.getCount()));
            menuStorageChooserContainer.setVisibility(View.GONE);
            shelfEditText.setText(String.valueOf(item.getShelf()));
            saveAddButton.setOnClickListener(view -> {
                if (invalidInputs()) return;
                Snackbar.make(requireActivity().findViewById(R.id.storageLayout), R.string.saving, Snackbar.LENGTH_SHORT).show();
                StorageController.getInstance().editStorageItem(
                        itemId,
                        Integer.parseInt(String.valueOf(countEditText.getText())),
                        String.valueOf(nameEditText.getText()),
                        Integer.parseInt(String.valueOf(shelfEditText.getText()))
                );
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            });
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

    private void clearAddLayout() {
        nameEditText.setText(null);
        nameEditText.clearFocus();
        countEditText.setText(null);
        countEditText.clearFocus();
        menuStorageChooserContainer.setVisibility(View.VISIBLE);
        menuStorageChooser.setText(null);
        menuStorageChooser.clearFocus();
        shelfEditText.setText(null);
        shelfEditText.clearFocus();
    }

}