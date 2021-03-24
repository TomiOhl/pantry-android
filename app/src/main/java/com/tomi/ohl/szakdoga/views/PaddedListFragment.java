package com.tomi.ohl.szakdoga.views;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

// Egy ListFragment, amiben az utolsó elem után van egy normál FAB-nyi magasságú padding.
public class PaddedListFragment extends ListFragment {
    public PaddedListFragment() {
        super();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bottomPadding = (int) (88 * displayMetrics.density);
        getListView().setPadding(0, 0, 0, bottomPadding);
        getListView().setClipToPadding(false);
    }
}
