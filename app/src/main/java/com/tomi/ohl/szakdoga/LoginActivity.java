package com.tomi.ohl.szakdoga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.utils.InputUtils;
import com.tomi.ohl.szakdoga.views.FamilyChooserBottomSheet;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private TextInputLayout emailEditTextContainer;
    private EditText passwordEditText;
    private TextInputLayout passwordEditTextContainer;
    private EditText passwordAgainEditText;
    private TextInputLayout passwordAgainEditTextContainer;
    private EditText displayNameEditText;
    private TextInputLayout displayNameEditTextContainer;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Beviteli mezők
        emailEditText = findViewById(R.id.editTextEmail);
        emailEditTextContainer = findViewById(R.id.editTextEmailContainer);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordEditTextContainer = findViewById(R.id.editTextPasswordContainer);
        passwordAgainEditText = findViewById(R.id.editTextPasswordAgain);
        passwordAgainEditTextContainer = findViewById(R.id.editTextPasswordAgainContainer);
        displayNameEditText = findViewById(R.id.editTextDisplayName);
        displayNameEditTextContainer = findViewById(R.id.editTextDisplayNameContainer);
        setInputTextWatchers();

        // Gombok
        Button registerButton = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.btnLogin);
        registerButton.setOnClickListener(view -> onRegisterClick());
        loginButton.setOnClickListener(view -> onLoginClick());
    }

    // A regisztráció gombra kattintva
    private void onRegisterClick() {
        // UI módosuljon regisztrációs módba
        if (displayNameEditTextContainer.getVisibility() == View.GONE) {
            displayNameEditTextContainer.setVisibility(View.VISIBLE);
            passwordAgainEditTextContainer.setVisibility(View.VISIBLE);
            loginButton.setText(R.string.back);
            loginButton.setBackgroundColor(getColor(android.R.color.white));
            loginButton.setTextColor(getColor(R.color.colorAccent));
            loginButton.setForeground(ContextCompat.getDrawable(this, R.drawable.ripple_button_secondary));
        } else
            createUser();
    }

    // A bejelentkezés gombra kattintva
    private void onLoginClick() {
        // UI módosuljon bejelentkezős módba
        if (displayNameEditTextContainer.getVisibility() == View.VISIBLE) {
            displayNameEditTextContainer.setVisibility(View.GONE);
            passwordAgainEditTextContainer.setVisibility(View.GONE);
            loginButton.setText(R.string.login);
            loginButton.setBackgroundColor(getColor(R.color.colorAccent));
            loginButton.setTextColor(getColor(android.R.color.white));
            loginButton.setForeground(null);
        } else
            loginUser();
    }

    private void createUser() {
        if (invalidInputs())
            return;
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Bejelentkezés sikeres, adjuk meg a teljes nevet is
                        FirebaseUser user = Objects.requireNonNull(mAuth.getCurrentUser());
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayNameEditText.getText().toString())
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    InputUtils.hideKeyboard(this);
                                    if (task1.isSuccessful()) {
                                        // Névbeállítás sikeres
                                        FamilyChooserBottomSheet familyChooser = new FamilyChooserBottomSheet();
                                        familyChooser.setCancelable(false);
                                        familyChooser.show(getSupportFragmentManager(), "initial_" + FamilyChooserBottomSheet.class.getSimpleName());
                                    }
                                });
                    } else {
                        // Sikertelen regisztráció esetén hibaüzenet
                        InputUtils.hideKeyboard(this);
                        Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.create_user_failed) + ": " + task.getException(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser() {
        if (invalidInputs())
            return;
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Bejelentkezés sikeres
                        setCurrentFamilyAndRedirect();
                    } else {
                        // Sikertelen bejelentkezés esetén hibaüzenet
                        InputUtils.hideKeyboard(this);
                        Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.login_failed) + ": " + task.getException(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void setCurrentFamilyAndRedirect() {
        FamilyController.getInstance().getFamily().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    HashMap<String, Object> familydata = (HashMap<String, Object>) document.getData();
                    assert familydata != null;
                    String family = (String) familydata.get("family");
                    FamilyController.getInstance().setCurrentFamily(family);
                    redirectToProfile();
                }
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

    private boolean invalidInputs() {
        // Üres inputok kivédése
        if (emailEditText.getText().toString().trim().isEmpty()) {
            emailEditTextContainer.setError(getString(R.string.require_email));
            emailEditText.requestFocus();
            return true;
        }
        if (passwordEditText.getText().toString().trim().isEmpty()) {
            passwordEditTextContainer.setError(getString(R.string.require_password));
            passwordEditText.requestFocus();
            return true;
        }
        // Regisztráció esetén ezek is kellenek
        if (displayNameEditTextContainer.getVisibility() == View.VISIBLE) {
            // Elég hosszú-e a jelszó
            if (passwordEditText.getText().toString().length() < 8) {
                passwordEditTextContainer.setError(getString(R.string.require_longer_password));
                passwordEditText.requestFocus();
                return true;
            }
            // Még egyszer meg kell adni a jelszót
            if (passwordAgainEditText.getText().toString().trim().isEmpty()) {
                passwordAgainEditTextContainer.setError(getString(R.string.require_password_again));
                passwordAgainEditText.requestFocus();
                return true;
            }
            // Egyeznek-e a jelszavak
            if (!passwordEditText.getText().toString().equals(passwordAgainEditText.getText().toString())) {
                InputUtils.hideKeyboard(this);
                passwordAgainEditTextContainer.setError(getString(R.string.require_matching_passwords));
                return true;
            }
            // Még egy input
            if (displayNameEditText.getText().toString().trim().isEmpty()) {
                displayNameEditTextContainer.setError(getString(R.string.require_name));
                displayNameEditText.requestFocus();
                return true;
            }
        }
        return false;
    }

    // Beviteli mezőkről hiba eltüntetése, ha írni kezdünk beléjük
    private void setInputTextWatchers() {
        InputUtils.clearInputLayoutErrors(emailEditTextContainer, emailEditText);
        InputUtils.clearInputLayoutErrors(passwordEditTextContainer, passwordEditText);
        InputUtils.clearInputLayoutErrors(passwordAgainEditTextContainer, passwordAgainEditText);
        InputUtils.clearInputLayoutErrors(displayNameEditTextContainer, displayNameEditText);
    }

}