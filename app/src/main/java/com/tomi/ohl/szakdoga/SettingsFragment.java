package com.tomi.ohl.szakdoga;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.tomi.ohl.szakdoga.controller.FamilyController;

public class SettingsFragment extends PreferenceFragmentCompat {

    private MainActivity mainActivity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()){
            case "logout":
                logout();
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void logout() {
        // Regisztrált db listenerek eltávolítása
        for(ListenerRegistration elem : ((MainActivity)requireActivity()).dbListeners) {
            elem.remove();
        }
        // User kijelentkeztetése
        FirebaseAuth.getInstance().signOut();
        FamilyController.getInstance().setCurrentFamily(null);
        // Átirányítás a login képernyőre
        startActivity(new Intent(mainActivity, LoginActivity.class));
        mainActivity.finish();
    }

}