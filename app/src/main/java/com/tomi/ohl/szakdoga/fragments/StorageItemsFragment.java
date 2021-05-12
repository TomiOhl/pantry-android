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
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.StorageRecylerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.LinkedHashMap;

/**
 * A tárolt elemeket megjelenítő fragment.
 */
public class StorageItemsFragment extends Fragment {
    private LinkedHashMap<String, StorageItem> itemsMap;
    private RecyclerView rv;
    private int mStorage;
    private ListenerRegistration dbListener;

    public StorageItemsFragment() {}

    /**
     * Új példány létrehozása a megadott tárolóval.
     * @param storage a megjelenítendő tároló.
     * @return a fragment egy példánya.
     */
    public static StorageItemsFragment newInstance(int storage) {
        Bundle args = new Bundle();
        args.putInt("storage", storage);
        StorageItemsFragment fragment = new StorageItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null)
            mStorage = args.getInt("storage");
        return inflater.inflate(R.layout.fragment_storage_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dbListener == null)
            loadStorageContents(mStorage);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbListener.remove();
        dbListener = null;
    }

    /**
     * Az adott tároló tartalmának figyelése, listázás és onClick beállítása/frissítése.
     * @param storage a tároló, aminek tartalmára kíváncsiak vagyunk.
     */
    private void loadStorageContents(int storage) {
        itemsMap = new LinkedHashMap<>();
        rv = requireView().findViewById(R.id.storageRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(requireView().getContext()));
        rv.setAdapter(new StorageRecylerViewAdapter(itemsMap));
        dbListener = StorageController.getInstance().getStorageItems(storage).addSnapshotListener(
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
    }

    /**
     * Listener/megjelenítés frissítése.
     */
    public void updateContent() {
        Bundle args = getArguments();
        if (args != null) {
            int storage = args.getInt("storage");
            loadStorageContents(storage);
        }
    }

    /**
     * Ha nincs találat, egy szöveget jelenítsünk meg.
     * @param resultsSize a találatok listájának hossza.
     */
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
