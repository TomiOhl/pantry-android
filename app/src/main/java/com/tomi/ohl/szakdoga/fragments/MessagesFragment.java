package com.tomi.ohl.szakdoga.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.NewMessageActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.MessagesRecyclerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.views.TopFadingEdgeRecyclerView;

import java.util.LinkedHashMap;

public class MessagesFragment extends Fragment {
    private LinkedHashMap<String, MessageItem> msgMap;
    private TopFadingEdgeRecyclerView rv;

    public MessagesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // A fragment layoutja
        View layout =  inflater.inflate(R.layout.fragment_messages, container, false);

        // Üzenetek listája RecyclerView-n
        msgMap = new LinkedHashMap<>();
        rv = (TopFadingEdgeRecyclerView) layout.findViewById(R.id.msgRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(layout.getContext()));
        rv.setAdapter(new MessagesRecyclerViewAdapter(msgMap));

        // Új üzenet gomb
        FloatingActionButton newMessageFab = layout.findViewById(R.id.fabNewMsg);
        newMessageFab.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), NewMessageActivity.class)));

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Üzenetek listájának lekérése
        ((MainActivity)requireActivity()).dbListeners.add(StorageController.getInstance().getNewMessages().addSnapshotListener(
                (value, error) -> {
                    assert value != null;
                    msgMap.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String id = doc.getId();
                        MessageItem item = doc.toObject(MessageItem.class);
                        msgMap.put(id, item);
                    }
                    if (rv.getAdapter() != null)
                        ((MessagesRecyclerViewAdapter)rv.getAdapter()).updateKeys(msgMap.keySet());
                    rv.scrollToPosition(msgMap.keySet().size() - 1);
                }
        ));
    }
}