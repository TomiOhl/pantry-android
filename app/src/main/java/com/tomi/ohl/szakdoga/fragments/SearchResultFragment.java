package com.tomi.ohl.szakdoga.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StorageRecylerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.LinkedHashMap;

public class SearchResultFragment extends Fragment {
    private LinkedHashMap<String, StorageItem> itemsMap;
    private RecyclerView rv;
    String query;

    public SearchResultFragment() {
    }

    public static SearchResultFragment newInstance(String query) {
        Bundle args = new Bundle();
        args.putString("query", query);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null && query == null)
            query = args.getString("query");
        return inflater.inflate(R.layout.fragment_storage_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStorageContents();
    }

    @Override
    public void onStop() {
        SearchView searchView = ((MainActivity) requireActivity()).getSearchView();
        if (!searchView.isIconified())
            try {
                searchView.onActionViewCollapsed();
            } catch (IllegalStateException e) {
                Log.d("COLLAPSESEARCHVIEW", "Failed to collapse search view. This happens normally on configchanges.");
            }
        super.onStop();
    }

    // Új kereséshez
    public void changeQuery(String query) {
        this.query = query;
        loadStorageContents();
    }

    // Az adott tároló tartalmának figyelése, listázás és onClick beállítása/frissítése
    private void loadStorageContents() {
        itemsMap = new LinkedHashMap<>();
        rv = requireView().findViewById(R.id.storageRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(requireView().getContext()));
        rv.setAdapter(new StorageRecylerViewAdapter(itemsMap));
        ((MainActivity) requireActivity()).getDbListeners().add(StorageController.getInstance().searchStorageItems(query).addSnapshotListener(
                (value, error) -> {
                    assert value != null;
                    itemsMap.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String id = doc.getId();
                        StorageItem item = doc.toObject(StorageItem.class);
                        itemsMap.put(id, item);
                    }
                    if (rv.getAdapter() != null)
                        ((StorageRecylerViewAdapter)rv.getAdapter()).updateKeys(itemsMap.keySet());
                    toggleEmptyText(itemsMap.keySet().size());
                }
        ));
    }

    // Ha nincs találat, egy szöveget jelenítsünk meg
    private void toggleEmptyText(int resultsSize) {
        TextView emptyText = requireView().findViewById(R.id.storageListEmpty);
        if (resultsSize == 0) {
            rv.setVisibility(View.GONE);
            emptyText.setText(R.string.search_no_result);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }
}