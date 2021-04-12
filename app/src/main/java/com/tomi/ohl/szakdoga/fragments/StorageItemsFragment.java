package com.tomi.ohl.szakdoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StorageRecylerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.LinkedHashMap;

public class StorageItemsFragment extends Fragment {
    private LinkedHashMap<String, StorageItem> itemsMap;
    private RecyclerView rv;
    private int storage;
    private String sortBy;
    private ListenerRegistration dbListener;

    public StorageItemsFragment() {}

    public static StorageItemsFragment newInstance(int storage, String sortBy) {
        Bundle args = new Bundle();
        args.putInt("storage", storage);
        args.putString("sortBy", sortBy);
        StorageItemsFragment fragment = new StorageItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null && sortBy == null) {
            storage = args.getInt("storage");
            sortBy = args.getString("sortBy");
        }
        return inflater.inflate(R.layout.fragment_storage_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dbListener == null)
            loadStorageContents(storage, sortBy);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbListener = null;
    }

    // Az adott tároló tartalmának figyelése, listázás és onClick beállítása/frissítése
    private void loadStorageContents(int storage, String sortBy) {
        itemsMap = new LinkedHashMap<>();
        rv = requireView().findViewById(R.id.storageRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(requireView().getContext()));
        rv.setAdapter(new StorageRecylerViewAdapter(itemsMap));
        dbListener = StorageController.getInstance().getStorageItems(storage, sortBy).addSnapshotListener(
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
        );
        ((MainActivity)requireActivity()).getDbListeners().add(dbListener);
    }

    public void setContent(String sortBy) {
        Bundle args = getArguments();
        if (args != null) {
            this.sortBy = sortBy;
            int storage = args.getInt("storage");
            loadStorageContents(storage, sortBy);
        }
    }

    // Ha nincs találat, egy szöveget jelenítsünk meg
    private void toggleEmptyText(int resultsSize) {
        View layout = getView();
        if (layout == null) return;
        TextView emptyText = layout.findViewById(R.id.storageListEmpty);
        if (resultsSize == 0) {
            rv.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }
}
