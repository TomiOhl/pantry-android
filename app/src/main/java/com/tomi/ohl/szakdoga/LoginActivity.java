package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;
    //private TextInputLayout passwordAgainEditTextContainer;   // ennek használatával custom errorokat lehet megjeleníteni, de több témázást igényel
    private TextInputLayout newFamilyTextInputLayout;
    private EditText displayNameEditText;
    private Button loginButton;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

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
        newFamilyTextInputLayout = findViewById(R.id.textInputLayoutNewFamily);
        TextInputEditText newFamilyEditText = findViewById(R.id.editTextNewFamily);

        Button registerButton = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.btnLogin);
        Button newFamilyButton = findViewById(R.id.btnNewFamily);
        Button existingFamilyButton = findViewById(R.id.btnExistingFamily);

        registerButton.setOnClickListener(view -> registerUser());
        loginButton.setOnClickListener(view -> loginUser());
        newFamilyButton.setOnClickListener(view -> setFamily(null));
        existingFamilyButton.setOnClickListener(view -> {
            String s = Objects.requireNonNull(newFamilyEditText.getText()).toString();
            if (!s.trim().isEmpty())
                setFamily(s);
        });

        // Családválaszto layout
        View bottom_sheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setHideable(false);
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
        if (!passwordEditText.getText().toString().equals(passwordAgainEditText.getText().toString())) {
            hideKeyboard();
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.passwords_dont_match, Snackbar.LENGTH_SHORT).show();
            // passwordAgainEditTextContainer.setError(getString(R.string.passwords_dont_match));
            return;
        }
        // TODO: check if editexts are empty
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
                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    hideKeyboard();
                                });
                    } else {
                        // Sikertelen regisztráció esetén hibaüzenet
                        hideKeyboard();
                        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.create_user_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // Regisztrációnál családbeállítás
    private void setFamily(String family_email) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> familyItem = new HashMap<>();
        if (family_email == null) {
            // új család létrehozása
            familyItem.put("family", userEmail);
            db.collection("Users").document(userEmail).set(familyItem)
                    .addOnSuccessListener(runnable -> redirectToProfile());
        } else {
            // létezik-e a család
            db.collection("Users").whereEqualTo("family", family_email).get().addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {
                                // Nem létezik, hiba
                                newFamilyTextInputLayout.setError(getString(R.string.family_does_not_exist));
                            } else {
                                // Van ilyen family, el lehet menteni a jelenlegi usert is ahhoz
                                familyItem.put("family", family_email);
                                db.collection("Users").document(userEmail).set(familyItem)
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
                        hideKeyboard();
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if(imm != null && focusedView != null)
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}