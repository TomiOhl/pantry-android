package com.tomi.ohl.szakdoga.utils;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {
    public static boolean setNightMode(String mode) {
        switch (mode) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                return true;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return true;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return true;
        }
        return false;
    }
}
