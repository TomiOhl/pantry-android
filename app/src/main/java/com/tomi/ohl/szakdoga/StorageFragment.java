package com.tomi.ohl.szakdoga;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StorageFragment extends Fragment {

    private TextView listTextView;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private TextInputEditText nameEditText;
    private TextInputEditText countEditText;
    private AutoCompleteTextView menuStorageChooser;
    private TextInputEditText shelfEditText;
    private FirebaseFirestore db;
    private FirebaseUser user;

    public StorageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A fragment layoutja
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        TextView nameTextView = layout.findViewById(R.id.textProfileName);
        TextView emailTextView = layout.findViewById(R.id.textProfileEmail);
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
            getTestList();
        }

        // Firestore read/write tesztelése
        addButton.setOnClickListener(view -> {
            // A user egyedi id-jével elnevezett dokumentumba beszúr egy adatot
            // A hozzáadás gomb többszöri megnyomása felülírja, de a kód működik több elemmel is
            Map<String, Object> randomitem = new HashMap<>();
            randomitem.put("timestamp", System.currentTimeMillis());
            assert user != null;
            db.collection("Users").document(user.getUid()).set(randomitem)
                    .addOnSuccessListener(runnable -> getTestList());
        });

        // Tárhelyválasztó tabek
        TabLayout storageChooser = layout.findViewById(R.id.tabStorageChooser);
        storageChooser.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Toast.makeText(getContext(), "Hu To Gep", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "Kamra", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getContext(), "Default", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Hozzáadás layout
        View bottom_sheet = layout.findViewById(R.id.bottom_sheet_add);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Hozzáadás layout gombjai
        cancelAddButton.setOnClickListener(view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        saveAddButton.setOnClickListener(view -> Toast.makeText(getContext(), "Mentés...", Toast.LENGTH_SHORT).show());

        // Hozzáadás layout tárhelyválasztó menüje
        String[] storageTypes = {"Huto", "Spajz"};
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

    private void getTestList() {
        // Kilistázza az adott user adatait
        StringBuilder userDataList = new StringBuilder();
        db.collection("Users").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    HashMap<String, Object> userdata = (HashMap<String, Object>) document.getData();
                    assert userdata != null;
                    for (String elem : userdata.keySet())
                        userDataList.append(elem).append(" => ").append(userdata.get(elem)).append("\n");
                    listTextView.setText(userDataList);
                }
            } else {
                Toast.makeText(getContext(), "Dokumentum lekérése sikertelen: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
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