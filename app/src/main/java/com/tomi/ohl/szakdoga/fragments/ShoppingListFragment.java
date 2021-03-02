package com.tomi.ohl.szakdoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.ShoppingListRecyclerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.views.AddShoppingListItemBottomSheet;
import com.tomi.ohl.szakdoga.views.TopFadingEdgeRecyclerView;

import java.util.LinkedHashMap;

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
        // Bevásárlólista RecyclerView-n
        shoppingListMap = new LinkedHashMap<>();
        rv = requireView().findViewById(R.id.shoppinglistRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(requireView().getContext()));
        rv.setAdapter(new ShoppingListRecyclerViewAdapter(shoppingListMap));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(rv);
        // Bevásárlólista lekérése
        ((MainActivity)requireActivity()).dbListeners.add(StorageController.getInstance().getShoppingListItems().addSnapshotListener(
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
                }
        ));
    }

    // RecyclerView-n való csúsztatás kezelése
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
                if (adapter != null)
                    StorageController.getInstance().deleteShoppingListItem(adapter.getKeyAtPosition(position));
            }
        };
    }
}