package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView nameTextView = findViewById(R.id.textProfileName);
        TextView emailTextView = findViewById(R.id.textProfileEmail);

        Button logoutButton = findViewById(R.id.btnLogout);

        // Kérjük le a bejelentkezett usert, majd jelenítsük meg az adatait
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            nameTextView.setText(String.format("%s: %s", getString(R.string.display_name), user.getDisplayName()));
            emailTextView.setText(String.format("%s: %s", getString(R.string.email), user.getEmail()));
        }

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();
        });
    }
}