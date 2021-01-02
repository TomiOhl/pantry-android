package com.tomi.ohl.szakdoga;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tomi.ohl.szakdoga.controller.FamilyController;

import java.util.Objects;

import static com.tomi.ohl.szakdoga.utils.ImeUtils.hideKeyboard;

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
    private EditText newFamilyEditText;
    private TextInputLayout newFamilyEditTextContainer;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

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
        passwordAgainEditTextContainer = findViewById(R.id.editTextPasswordAgainContainer);
        passwordAgainEditText = findViewById(R.id.editTextPasswordAgain);
        displayNameEditTextContainer = findViewById(R.id.editTextDisplayNameContainer);
        displayNameEditText = findViewById(R.id.editTextDisplayName);
        newFamilyEditText = findViewById(R.id.editTextNewFamily);
        newFamilyEditTextContainer = findViewById(R.id.editTextNewFamilyContainer);
        setInputTextWatchers();

        // Gombok
        Button registerButton = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.btnLogin);
        Button newFamilyButton = findViewById(R.id.btnNewFamily);
        Button existingFamilyButton = findViewById(R.id.btnExistingFamily);

        registerButton.setOnClickListener(view -> registerUser());
        loginButton.setOnClickListener(view -> loginUser());
        newFamilyButton.setOnClickListener(view -> setFamily(null));
        existingFamilyButton.setOnClickListener(view -> {
            String s = newFamilyEditText.getText().toString();
            if (!s.trim().isEmpty())
                setFamily(s);
        });

        // Családválasztó layout
        View bottom_sheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setDraggable(false);
    }

    // A regisztráció gombra kattintva
    private void registerUser() {
        // UI módosuljon regisztrációs módba
        if (displayNameEditTextContainer.getVisibility() == View.GONE) {
            displayNameEditTextContainer.setVisibility(View.VISIBLE);
            passwordAgainEditTextContainer.setVisibility(View.VISIBLE);
            loginButton.setText(R.string.back);
        } else
            createUser();
    }

    private void createUser() {
        if (invalidInputs())
            return;
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Bejelentkezés sikeres, adjuk meg a teljes nevet is
                        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayNameEditText.getText().toString())
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful())
                                        // Névbeállítás sikeres
                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    hideKeyboard(this);
                                });
                    } else {
                        // Sikertelen regisztráció esetén hibaüzenet
                        hideKeyboard(this);
                        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.create_user_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // Regisztrációnál családbeállítás
    private void setFamily(String family_email) {
        FirebaseUser user = Objects.requireNonNull(mAuth.getCurrentUser());
        String userEmail = Objects.requireNonNull(user.getEmail());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (family_email == null) {
            // új család létrehozása
            FamilyController.getInstance().setFamily(userEmail,  userEmail)
                    .addOnSuccessListener(runnable -> redirectToProfile());
        } else {
            // létezik-e a család
            FamilyController.getInstance().exists(family_email).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() == 0) {
                                // Nem létezik, hiba
                                newFamilyEditTextContainer.setError(getString(R.string.family_does_not_exist));
                            } else {
                                // Van ilyen family, el lehet menteni a jelenlegi usert is ahhoz
                                FamilyController.getInstance().setFamily(userEmail, family_email)
                                        .addOnSuccessListener(runnable -> redirectToProfile());
                            }
                        } else {
                            Toast.makeText(this, "Ellenőrzés sikertelen: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    // A bejelentkezés gombra kattintva
    private void loginUser() {
        if (displayNameEditTextContainer.getVisibility() == View.VISIBLE) {
            displayNameEditTextContainer.setVisibility(View.GONE);
            passwordAgainEditTextContainer.setVisibility(View.GONE);
            loginButton.setText(R.string.login);
        } else {
            if (invalidInputs())
                return;
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Bejelentkezés sikeres
                            redirectToProfile();
                        } else {
                            // Sikertelen bejelentkezés esetén hibaüzenet
                            hideKeyboard(this);
                            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.login_failed, Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
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
            if (passwordAgainEditText.getText().toString().trim().isEmpty()) {
                passwordAgainEditTextContainer.setError(getString(R.string.require_password_again));
                passwordAgainEditText.requestFocus();
                return true;
            }
            // Elég hosszú-e a jelszó
            if (passwordEditText.getText().toString().length() < 8) {
                passwordEditTextContainer.setError(getString(R.string.require_longer_password));
                passwordEditText.requestFocus();
                return true;
            }
            // Egyeznek-e a jelszavak
            if (!passwordEditText.getText().toString().equals(passwordAgainEditText.getText().toString())) {
                hideKeyboard(this);
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
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailEditTextContainer.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordEditTextContainer.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordAgainEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordAgainEditTextContainer.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        displayNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                displayNameEditTextContainer.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newFamilyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newFamilyEditTextContainer.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}