package com.tomi.ohl.szakdoga.utils;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Témakezelő segédosztály.
 */
public class ThemeUtils {
    /**
     * Színséma beállítása a beépített DayNight téma lehetőségeit kihasználva.
     * @param mode system/light/dark.
     * @return sikerrel járt-e az átállítás.
     */
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
