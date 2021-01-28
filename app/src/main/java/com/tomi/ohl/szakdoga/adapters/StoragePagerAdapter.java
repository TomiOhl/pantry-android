package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;

import com.tomi.ohl.szakdoga.R;

public class StoragePagerAdapter extends FragmentPagerAdapter {
    Context ctx;
    ListFragment fridgeListFragment, pantryListFragment;

    public StoragePagerAdapter(FragmentManager fm, Context ctx, ListFragment fridgeListFragment, ListFragment pantryListFragment) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.ctx = ctx;
        this.fridgeListFragment = fridgeListFragment;
        this.pantryListFragment = pantryListFragment;
    }

    @NonNull
    @Override
    public ListFragment getItem(int position) {
        if (position == 0){
            return fridgeListFragment;
        } else {
            return pantryListFragment;
        }
    }

    // Visszaadja, hány lap van. Később, ha ezek száma dinamikus lesz, módosítani kell
    @Override
    public int getCount() {
        return 2;
    }

    // Lapok címei. Később, ha ezek száma dinamikus lesz, módosítani kell
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return ctx.getString(R.string.fridge);
            case 1:
                return ctx.getString(R.string.pantry);
        }
        return null;
    }
}