package com.tomi.ohl.szakdoga;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.ListenerRegistration;
import com.tomi.ohl.szakdoga.fragments.MessagesFragment;
import com.tomi.ohl.szakdoga.fragments.SettingsFragment;
import com.tomi.ohl.szakdoga.fragments.ShoppingListFragment;
import com.tomi.ohl.szakdoga.fragments.StorageFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    public ArrayList<ListenerRegistration> dbListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbListeners = new ArrayList<>();

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
        if (savedInstanceState == null)
            chooseFragment(new StorageFragment());
    }

    // a kiválasztott fragment betöltése
    public void chooseFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, fragment.getClass().getSimpleName());
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Regisztrált db listenerek eltávolítása
        for(ListenerRegistration elem : dbListeners) {
            elem.remove();
        }
    }

    // toolbar-ikon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            Toast.makeText(this, "Keresés az éléskamrában", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}