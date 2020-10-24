package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText displayNameEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        displayNameEditText = findViewById(R.id.editTextDisplayName);

        Button registerButton = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.btnLogin);

        registerButton.setOnClickListener(view -> registerUser());

        loginButton.setOnClickListener(view -> loginUser());

    }

    @Override
    public void onStart() {
        super.onStart();
        // Ellenőrizzük, be van-e jelentkezve a user
        redirectToProfile();
    }

    private void registerUser() {
        // UI módosuljon regisztrációs módba
        if (displayNameEditText.getVisibility() == View.GONE) {
            displayNameEditText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else
            createUser();
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Bejelentkezés sikeres, adjuk meg a teljes nevet is
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayNameEditText.getText().toString())
                                .build();
                        assert user != null;
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful())
                                        // Névbeállítás sikeres
                                        // TODO: létrehozni egy dokumentumot a usernek
                                        redirectToProfile();
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Felhasználó létrehozása sikertelen.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser() {
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Bejelentkezés sikeres
                        redirectToProfile();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Bejelentkezés sikertelen.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void redirectToProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
    }

}