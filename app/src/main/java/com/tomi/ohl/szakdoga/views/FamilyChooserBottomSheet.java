package com.tomi.ohl.szakdoga.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.utils.InputUtils;

import java.util.Objects;

public class FamilyChooserBottomSheet extends BottomSheetDialogFragment {

    private EditText newFamilyEditText;
    private TextInputLayout newFamilyEditTextContainer;

    public FamilyChooserBottomSheet() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedBottomSheetStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A sheet layoutja
        View layout = inflater.inflate(R.layout.bottom_sheet_family, container, false);

        newFamilyEditText = layout.findViewById(R.id.editTextNewFamily);
        newFamilyEditTextContainer = layout.findViewById(R.id.editTextNewFamilyContainer);
        InputUtils.clearInputLayoutErrors(newFamilyEditTextContainer, newFamilyEditText);

        Button newFamilyButton = layout.findViewById(R.id.btnNewFamily);
        Button existingFamilyButton = layout.findViewById(R.id.btnExistingFamily);
        newFamilyButton.setOnClickListener(view -> setFamily(null));
        existingFamilyButton.setOnClickListener(view -> {
            String s = newFamilyEditText.getText().toString();
            if (!s.trim().isEmpty())
                setFamily(s);
        });

        return layout;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Ha regisztráltunk, akkor ezután irány a profil
        if (this.getTag() != null && this.getTag().startsWith("initial"))
            redirectToProfile();
    }

    // Regisztrációnál családbeállítás
    private void setFamily(String family_email) {
        // Ha beállításokból jöttünk, regisztrált db listenerek eltávolítása
        if (this.getTag() != null && this.getTag().startsWith("settings"))
            ((MainActivity)requireActivity()).removeDbListeners();
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        String userEmail = Objects.requireNonNull(user.getEmail());
        if (family_email == null) {
            // új család létrehozása
            FamilyController.getInstance().setFamily(userEmail,  userEmail)
                    .addOnSuccessListener(runnable -> {
                        FamilyController.getInstance().setCurrentFamily(userEmail);
                        dismiss();
                    });
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
                                    .addOnSuccessListener(runnable -> {
                                        FamilyController.getInstance().setCurrentFamily(family_email);
                                        ((MainActivity)requireActivity()).checkLastSeenMsg();
                                        dismiss();
                                    });
                        }
                    } else {
                        Toast.makeText(this.requireContext(), "Ellenőrzés sikertelen: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }
    }

    private void redirectToProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this.requireActivity(), MainActivity.class));
            this.requireActivity().finish();
        }
    }

}