package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;
    //private TextInputLayout passwordAgainEditTextContainer;   // ennek használatával custom errorokat lehet megjeleníteni, de több témázást igényel
    private EditText displayNameEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        //passwordAgainEditTextContainer = findViewById(R.id.editTextPasswordAgainContainer);
        passwordAgainEditText = findViewById(R.id.editTextPasswordAgain);
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

    // A regisztráció gombra kattintva
    private void registerUser() {
        // UI módosuljon regisztrációs módba
        if (displayNameEditText.getVisibility() == View.GONE) {
            displayNameEditText.setVisibility(View.VISIBLE);
            passwordAgainEditText.setVisibility(View.VISIBLE);
            loginButton.setText(R.string.back);
        } else
            createUser();
    }

    private void createUser() {
        if (!passwordEditText.getText().equals(passwordAgainEditText.getText())) {
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.passwords_dont_match, Snackbar.LENGTH_SHORT).show();
            // passwordAgainEditTextContainer.setError(getString(R.string.passwords_dont_match));
            return;
        }
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
                                        redirectToProfile();
                                });
                    } else {
                        // Sikertelen regisztráció esetén hibaüzenet
                        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.create_user_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // A bejelentkezés gombra kattintva
    private void loginUser() {
        if (displayNameEditText.getVisibility() == View.VISIBLE) {
            displayNameEditText.setVisibility(View.GONE);
            passwordAgainEditText.setVisibility(View.GONE);
            loginButton.setText(R.string.login);
        } else
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Bejelentkezés sikeres
                        redirectToProfile();
                    } else {
                        // Sikertelen bejelentkezés esetén hibaüzenet
                        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.login_failed, Snackbar.LENGTH_SHORT).show();
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