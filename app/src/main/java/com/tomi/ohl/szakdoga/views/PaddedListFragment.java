package com.tomi.ohl.szakdoga.views;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

// Egy ListFragment, amiben az utolsó elem után van egy normál FAB-nyi magasságú padding.
public class PaddedListFragment extends ListFragment {
    private String emptyText;

    public PaddedListFragment() {}

    public PaddedListFragment(String emptyText) {
        super();
        this.emptyText = emptyText;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Üres lista esetén megjelenítendő szöveg
        setEmptyText(emptyText);
        // Padding beállítása a lista alá
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bottomPadding = (int) (88 * displayMetrics.density);
        getListView().setPadding(0, 0, 0, bottomPadding);
        getListView().setClipToPadding(false);
    }
}
