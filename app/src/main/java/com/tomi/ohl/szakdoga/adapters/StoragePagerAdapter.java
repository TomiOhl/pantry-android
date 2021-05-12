package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tomi.ohl.szakdoga.R;

/**
 * A tárolókat megjelenítő fragmentet magába foglaló ViewPager adaptere.
 */
public class StoragePagerAdapter extends FragmentPagerAdapter {
    Context ctx;
    Fragment fridgeListFragment, pantryListFragment;

    public StoragePagerAdapter(FragmentManager fm, Context ctx, Fragment fridgeListFragment, Fragment pantryListFragment) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.ctx = ctx;
        this.fridgeListFragment = fridgeListFragment;
        this.pantryListFragment = pantryListFragment;
    }

    /**
     * Egy adott lap tartalmának lekérése.
     * @param position a lap pozíciója.
     * @return a lap tartalma, vagyis egy fragment.
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return fridgeListFragment;
        } else {
            return pantryListFragment;
        }
    }

    /**
     * Visszaadja, hány lap van.
     * Később, ha ezek száma dinamikus lesz, módosítani kell.
     * @return a lapok száma.
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Egy lap címének lekérése.
     * Később, ha ezek száma dinamikus lesz, módosítani kell.
     * @param position hányadik pozíción lévő lapra vagyunk kíváncsiak.
     * @return a lap címe.
     */
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