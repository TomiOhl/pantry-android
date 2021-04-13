package com.tomi.ohl.szakdoga;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.fragments.MessagesFragment;
import com.tomi.ohl.szakdoga.fragments.SearchResultFragment;
import com.tomi.ohl.szakdoga.fragments.SettingsFragment;
import com.tomi.ohl.szakdoga.fragments.ShoppingListFragment;
import com.tomi.ohl.szakdoga.fragments.StorageFragment;
import com.tomi.ohl.szakdoga.utils.NotificationUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private String selectedFragment;
    private SearchView searchView;
    private ArrayList<ListenerRegistration> dbListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbListeners = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Értesítési csatorna létrehozása
        NotificationUtils.createNotificationChannel(this);

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
                chooseInitialFragment("StorageFragment");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLastSeenMsg();
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

    @Override
    public void onBackPressed() {
        if (searchView.isIconified())
            super.onBackPressed();
        else
            searchView.onActionViewCollapsed();
    }

    // a kiválasztott fragment betöltése
    public void chooseFragment(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, tag);
        //transaction.addToBackStack(null);
        transaction.commit();
        setSelectedFragment(tag);
    }

    // Megnyitás utáni első fragment betöltése
    private void chooseInitialFragment(String destinationFromShortcut) {
        int selectedId = R.id.storage;
        switch (destinationFromShortcut) {
            case "ShoppingListFragment":
                selectedId = R.id.shoppinglist;
                break;
            case "MessagesFragment":
                selectedId = R.id.messages;
                break;
            case "SettingsFragment":
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

    public ArrayList<ListenerRegistration> getDbListeners() {
        return dbListeners;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public BottomNavigationView getBottomNavigation() {
        return bottomNavigation;
    }

    // Erre térünk vissza a keresés fragmentról
    private void setSelectedFragment(String selectedFragment) {
        if (!selectedFragment.equals("SearchResultFragment"))
            this.selectedFragment = selectedFragment;
    }

    // ActionBaron lévő keresés viselkedése
    private void setupSearch(MenuItem menuItem) {
        searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(findViewById(R.id.mainLayout).getMeasuredWidth());
        searchView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setQueryHint(getString(R.string.search_storages));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchResultFragment searchResultFragment = (SearchResultFragment) getSupportFragmentManager().findFragmentByTag("SearchResultFragment");
                if (searchResultFragment == null) {
                    chooseFragment(SearchResultFragment.newInstance(query));
                    bottomNavigation.setVisibility(View.GONE);
                } else {
                    searchResultFragment.changeQuery(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText) && getSupportFragmentManager().findFragmentByTag("StorageFragment") == null) {
                    bottomNavigation.setVisibility(View.VISIBLE);
                    chooseInitialFragment(selectedFragment);
                }
                return true;
            }
        });
    }

    // Legutolsó üzenet idejének összehasonlítása az utoljára látottal
    public void checkLastSeenMsg() {
        // Legutolsó üzenet lekérése
        StorageController.getInstance().getLastMessage().addOnSuccessListener(msgSnapshot -> {
            if (msgSnapshot != null && msgSnapshot.size() > 0) {
                long lastMsgTime = (long) msgSnapshot.getDocuments().get(0).get("date");
                // Utoljára látott üzenet időpontja
                FamilyController.getInstance().getAccountInfo().addOnCompleteListener(task -> {
                    long lastSeenTime = 0;
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            Object result = documentSnapshot.get("lastSeen");
                            if (result != null)
                                lastSeenTime = (long) result;
                        }
                    }
                    // Legutolsó üzenet idejének összehasonlítása az utoljára látottal
                    BadgeDrawable badge = getBottomNavigation().getOrCreateBadge(R.id.messages);
                    if (lastMsgTime > lastSeenTime) {
                        NotificationUtils.showNewMessageNotification(this);
                        badge.setVisible(true);
                    } else {
                        NotificationUtils.clearNotifications(this);
                        badge.setVisible(false);
                    }
                });
            }
        });
    }

}