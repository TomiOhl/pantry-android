package com.tomi.ohl.szakdoga;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.ListenerRegistration;
import com.tomi.ohl.szakdoga.fragments.MessagesFragment;
import com.tomi.ohl.szakdoga.fragments.SearchResultFragment;
import com.tomi.ohl.szakdoga.fragments.SettingsFragment;
import com.tomi.ohl.szakdoga.fragments.ShoppingListFragment;
import com.tomi.ohl.szakdoga.fragments.StorageFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    public ArrayList<ListenerRegistration> dbListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbListeners = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // alsó navigáció beállítása
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.storage:
                    if (getSupportFragmentManager().findFragmentByTag("StorageFragment") == null)
                        chooseFragment(new StorageFragment());
                    return true;
                case R.id.shoppinglist:
                    if (getSupportFragmentManager().findFragmentByTag("ShoppingListFragment") == null)
                        chooseFragment(new ShoppingListFragment());
                    return true;
                case R.id.messages:
                    if (getSupportFragmentManager().findFragmentByTag("MessagesFragment") == null)
                        chooseFragment(new MessagesFragment());
                    return true;
                case R.id.settings:
                    if (getSupportFragmentManager().findFragmentByTag("SettingsFragment") == null)
                        chooseFragment(new SettingsFragment());
                    return true;
            }
            return false;
        });
        if (savedInstanceState == null) {
            String destinationFromShortcut = getIntent().getStringExtra("shortcut_destination");
            if (destinationFromShortcut != null)
                chooseInitialFragment(destinationFromShortcut);
            else
                chooseInitialFragment("storage");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeDbListeners();
    }

    // toolbar-ikon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        setupSearch(menu.findItem(R.id.action_search));
        return true;
    }

    // a kiválasztott fragment betöltése
    public void chooseFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, fragment.getClass().getSimpleName());
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    // Megnyitás utáni első fragment betöltése
    private void chooseInitialFragment(String destinationFromShortcut) {
        int selectedId = R.id.storage;
        switch (destinationFromShortcut) {
            case "shoppinglist":
                selectedId = R.id.shoppinglist;
                break;
            case "messages":
                selectedId = R.id.messages;
                break;
            case "settings":
                selectedId = R.id.settings;
        }
        bottomNavigation.setSelectedItemId(selectedId);
    }

    // Regisztrált db listenerek eltávolítása
    public void removeDbListeners() {
        for(ListenerRegistration elem : dbListeners)
            elem.remove();
        dbListeners.clear();
    }

    // ActionBaron lévő keresés viselkedése
    private void setupSearch(MenuItem menuItem) {
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(findViewById(R.id.mainLayout).getMeasuredWidth());
        searchView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setQueryHint(getString(R.string.search_storages));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                SearchResultFragment searchFragment = new SearchResultFragment();
                searchFragment.setArguments(bundle);
                chooseFragment(searchFragment);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText) && getSupportFragmentManager().findFragmentByTag("StorageFragment") == null)
                    chooseFragment(new StorageFragment());
                return true;
            }
        });
    }

}