package com.tomi.ohl.szakdoga;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StorageFragment extends Fragment {

    private MainActivity mainActivity;
    private TextView listTextView;
    private FirebaseFirestore db;
    private FirebaseUser user;

    public StorageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
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

        Button addButton = layout.findViewById(R.id.btnTestAdd);
        Button logoutButton = layout.findViewById(R.id.btnLogout);

        // Jelenítsük meg a bejelentkezett user adatait
        user = FirebaseAuth.getInstance().getCurrentUser();
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

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(mainActivity, LoginActivity.class));
            mainActivity.finish();
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

}