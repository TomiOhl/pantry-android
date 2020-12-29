package com.tomi.ohl.szakdoga;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;

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
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(mainActivity, LoginActivity.class));
        mainActivity.finish();
    }

}