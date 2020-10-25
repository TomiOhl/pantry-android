package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private TextView listTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        TextView nameTextView = findViewById(R.id.textProfileName);
        TextView emailTextView = findViewById(R.id.textProfileEmail);
        listTextView = findViewById(R.id.textTestList);

        Button addButton = findViewById(R.id.btnTestAdd);
        Button logoutButton = findViewById(R.id.btnLogout);

        // Kérjük le a bejelentkezett usert, majd jelenítsük meg az adatait
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
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();
        });
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
                Toast.makeText(getApplicationContext(), "Dokumentum lekérése sikertelen: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}