package com.tomi.ohl.szakdoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.ListFragment;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StorageListAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.LinkedHashMap;

public class SearchResultFragment extends ListFragment {
    public SearchResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null)
            loadStorageContents(args.getString("query"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result_list, container, false);
    }

    @Override
    public void onStop() {
        SearchView searchView = ((MainActivity) requireActivity()).getSearchView();
        if (!searchView.isIconified())
            searchView.onActionViewCollapsed();
        super.onStop();
    }

    // Új kereséshez
    public void changeQuery(String query) {
        loadStorageContents(query);
    }

    // Az adott tároló tartalmának figyelése, listázás és onClick beállítása/frissítése
    private void loadStorageContents(String query) {
        LinkedHashMap<String, StorageItem> itemsMap = new LinkedHashMap<>();
        StorageListAdapter listAdapter = new StorageListAdapter(this.requireContext(), itemsMap);
        this.setListAdapter(listAdapter);
        ((MainActivity) requireActivity()).getDbListeners().add(StorageController.getInstance().searchStorageItems(query).addSnapshotListener(
                (value, error) -> {
                    assert value != null;
                    itemsMap.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String id = doc.getId();
                        StorageItem item = doc.toObject(StorageItem.class);
                        itemsMap.put(id, item);
                    }
                    listAdapter.updateKeys(itemsMap.keySet());
                }
        ));
    }
}