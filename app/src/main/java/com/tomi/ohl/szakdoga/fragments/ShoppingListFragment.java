package com.tomi.ohl.szakdoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.ShoppingListRecyclerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
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
        newMessageFab.setOnClickListener(view ->
                Toast.makeText(requireContext(), "Add item", Toast.LENGTH_SHORT).show());

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
}