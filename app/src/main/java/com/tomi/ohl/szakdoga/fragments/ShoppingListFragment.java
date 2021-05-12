package com.tomi.ohl.szakdoga.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.ShoppingListRecyclerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.utils.DialogUtils;
import com.tomi.ohl.szakdoga.views.AddShoppingListItemBottomSheet;
import com.tomi.ohl.szakdoga.views.TopFadingEdgeRecyclerView;

import java.util.LinkedHashMap;

/**
 * Bevásárlólista megjelenítése és szerkesztése egy fragmenten.
 */
public class ShoppingListFragment extends Fragment {
    private LinkedHashMap<String, ShoppingListItem> shoppingListMap;
    private TopFadingEdgeRecyclerView rv;

    public ShoppingListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A fragment layoutja
        View layout = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // Hint megjelenítése, csak álló módban
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            TextView descTextView = layout.findViewById(R.id.shoppingListDesc);
            descTextView.setVisibility(View.GONE);
        }

        // Javaslatok megjelenítése
        ChipGroup chipGroup = layout.findViewById(R.id.shoppinglistSuggestionsGroup);
        loadSuggestions(chipGroup);

        // Új üzenet gomb
        FloatingActionButton newMessageFab = layout.findViewById(R.id.fabAddShopping);
        newMessageFab.setOnClickListener(view -> {
            AddShoppingListItemBottomSheet addItemSheet = new AddShoppingListItemBottomSheet();
            addItemSheet.show(getChildFragmentManager(), AddShoppingListItemBottomSheet.class.getSimpleName());
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadShoppingList();
    }

    /**
     * 5 javaslat betöltése, többi törlése.
     * @param chipGroup ahol a javaslatok megjelenjenek.
     */
    private void loadSuggestions(ChipGroup chipGroup) {
        StorageController.getInstance().getSuggestionItems().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && querySnapshot.size() > 0) {
                    int count = 0;
                    for (DocumentSnapshot elem : querySnapshot) {
                        count++;
                        if (count < 6) {
                            try {
                                String itemName = (String) elem.get("name");
                                Chip suggestion = (Chip) getLayoutInflater().inflate(R.layout.chip_suggestion, chipGroup, false);
                                suggestion.setText(itemName);
                                suggestion.setCheckable(false);
                                suggestion.setOnClickListener(view -> onSuggestionClicked(elem.getId(), chipGroup, suggestion, itemName));
                                suggestion.setOnCloseIconClickListener(view -> onCloseSuggestion(elem.getId(), chipGroup, suggestion));
                                chipGroup.addView(suggestion);
                            } catch (IllegalStateException e) {
                                Log.d("ADDCHIP", "Tried to add chip too late. The user probably didn't encounter any issues.");
                            }
                        } else {
                            StorageController.getInstance().deleteSuggestionItem(elem.getId());
                        }
                    }
                }
            }
        });
    }

    /**
     * Javaslatra kattintáskor annak bevásárlólistához adása.
     * @param itemId a beszúrandó javaslat id-je.
     * @param chipGroup ahonnan lekerül a javaslat.
     * @param chip ami a javaslatot megjelenítette.
     * @param itemName a javaslat neve.
     */
    private void onSuggestionClicked(String itemId, ChipGroup chipGroup, Chip chip, String itemName) {
        StorageController.getInstance().insertShoppingListItem(new ShoppingListItem(itemName, false));
        onCloseSuggestion(itemId, chipGroup, chip);
    }

    /**
     * Javaslat bezárásakor annak törlése.
     * @param itemId a törlendő javaslat id-je.
     * @param chipGroup ahonnan lekerül a javaslat.
     * @param chip ami a javaslatot megjelenítette.
     */
    private void onCloseSuggestion(String itemId, ChipGroup chipGroup, Chip chip) {
        chipGroup.removeView(chip);
        StorageController.getInstance().deleteSuggestionItem(itemId);
    }

    /**
     * Bevásárlólista betöltése és megjelenítése, figyelése.
     */
    private void loadShoppingList() {
        shoppingListMap = new LinkedHashMap<>();
        rv = requireView().findViewById(R.id.shoppinglistRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(requireView().getContext()));
        rv.setAdapter(new ShoppingListRecyclerViewAdapter(shoppingListMap));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(rv);
        // Bevásárlólista lekérése
        ((MainActivity)requireActivity()).getDbListeners().add(StorageController.getInstance().getShoppingListItems().addSnapshotListener(
                (value, error) -> {
                    assert value != null;
                    shoppingListMap.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String id = doc.getId();
                        ShoppingListItem item = doc.toObject(ShoppingListItem.class);
                        shoppingListMap.put(id, item);
                    }
                    if (rv.getAdapter() != null)
                        ((ShoppingListRecyclerViewAdapter)rv.getAdapter()).updateKeys(shoppingListMap.keySet());
                    if (value.size() > 0)
                        firstUseHint();
                }
        ));
    }

    /**
     * RecyclerView-n való csúsztatás kezelése.
     * @return a csúsztatásra reagáló callback.
     */
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // A csúsztatott elem törlése
                int position = viewHolder.getAdapterPosition();
                ShoppingListRecyclerViewAdapter adapter = (ShoppingListRecyclerViewAdapter) rv.getAdapter();
                if (adapter != null) {
                    String key = adapter.getKeyAtPosition(position);
                    adapter.setLastDeletedItemPosition(position);
                    StorageController.getInstance().deleteShoppingListItem(key);
                }
            }
        };
    }

    /**
     * Egy kis segítség megjelenítése első használatkor.
     */
    private void firstUseHint() {
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
            boolean firstUse = sharedPreferences.getBoolean("firstUsedShoppingList", true);
            if (firstUse) {
                DialogUtils.showFirstUseDialog(requireActivity(), getString(R.string.first_use_shopping_list));
                sharedPreferences.edit().putBoolean("firstUsedShoppingList", false).apply();
            }
        }
    }
}