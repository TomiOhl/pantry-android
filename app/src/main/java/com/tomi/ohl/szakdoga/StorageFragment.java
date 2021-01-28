package com.tomi.ohl.szakdoga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.adapters.StorageListAdapter;
import com.tomi.ohl.szakdoga.adapters.StoragePagerAdapter;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class StorageFragment extends Fragment {

    private TextView listTextView;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private TextInputEditText nameEditText;
    private TextInputEditText countEditText;
    private AutoCompleteTextView menuStorageChooser;
    private TextInputEditText shelfEditText;
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

        nameEditText = layout.findViewById(R.id.editTextAddItemName);
        countEditText = layout.findViewById(R.id.editTextAddItemVolume);
        menuStorageChooser = layout.findViewById(R.id.menuStoragechooser);
        shelfEditText = layout.findViewById(R.id.editTextAddItemShelf);

        Button addButton = layout.findViewById(R.id.btnTestAdd);
        Button saveAddButton = layout.findViewById(R.id.btnSaveAdd);
        Button cancelAddButton = layout.findViewById(R.id.btnCancelAdd);

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

        // Hozzáadás layout gombjai
        cancelAddButton.setOnClickListener(view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        saveAddButton.setOnClickListener(view -> {
            Snackbar.make(requireActivity().findViewById(R.id.storageLayout), R.string.saving, Snackbar.LENGTH_SHORT).show();
            StorageItem item = new StorageItem(
                Objects.requireNonNull(nameEditText.getText()).toString(),
                Integer.parseInt(String.valueOf(countEditText.getText())),
                menuStorageChooser.getText().toString(),
                Integer.parseInt(String.valueOf(shelfEditText.getText())),
                System.currentTimeMillis()
            );
            StorageController.getInstance().insertStorageItem(item);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        // Hozzáadás layout tárhelyválasztó menüje
        // TODO: Megcsinálni úgy, hogy rendesen használható legyen több nyelven is
        String[] storageTypes = {getString(R.string.fridge), getString(R.string.pantry)};
        ArrayAdapter<String> storageTypeAdapter = new ArrayAdapter<>(requireContext(), R.layout.menu_add_choose_storage, storageTypes);
        menuStorageChooser.setAdapter(storageTypeAdapter);

        // Hozzáadás FAB
        FloatingActionButton addFab = layout.findViewById(R.id.fabAdd);
        addFab.setOnClickListener(view -> {
            clearAddLayout();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Hűtőszekrény és kamra kilistázása
        loadFridgeContents(fridgeListFragment, getString(R.string.fridge));
        loadFridgeContents(pantryListFragment, getString(R.string.pantry));
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
    private void loadFridgeContents(ListFragment listFragment, String storage) {
        ArrayList<StorageItem> itemsList = new ArrayList<>();
        StorageListAdapter listAdapter = new StorageListAdapter(this.requireContext(), itemsList);
        listFragment.setListAdapter(listAdapter);
        StorageController.getInstance().getStorageItems(storage).addSnapshotListener(
                (value, error) -> {
                    assert value != null;
                    itemsList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        StorageItem item = doc.toObject(StorageItem.class);
                        itemsList.add(item);
                    }
                    listAdapter.notifyDataSetChanged();
                }
        );
    }

    private void clearAddLayout() {
        nameEditText.setText(null);
        nameEditText.clearFocus();
        countEditText.setText(null);
        countEditText.clearFocus();
        menuStorageChooser.setText(null);
        menuStorageChooser.clearFocus();
        shelfEditText.setText(null);
        shelfEditText.clearFocus();
    }

}