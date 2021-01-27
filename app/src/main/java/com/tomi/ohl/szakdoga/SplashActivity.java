package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tomi.ohl.szakdoga.controller.FamilyController;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Van-e bejelentkezett user?
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Nincs, irány bejelentkezni
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // Van, kérjük le a családját, majd irány a főképernyő
            FamilyController.getInstance().getFamily().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        HashMap<String, Object> familydata = (HashMap<String, Object>) document.getData();
                        assert familydata != null;
                        FamilyController.getInstance().setCurrentFamily((String) familydata.get("family"));
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }
            });
        }
    }
}