package com.tomi.ohl.szakdoga;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.adapters.MessagesRecyclerViewAdapter;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.MessageItem;

import java.util.ArrayList;
import java.util.Objects;

public class MessagesFragment extends Fragment {
    private ArrayList<MessageItem> msgList;
    private RecyclerView rv;

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
        msgList = new ArrayList<>();
        rv = layout.findViewById(R.id.msgRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(layout.getContext()));
        rv.setAdapter(new MessagesRecyclerViewAdapter(msgList));

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
        StorageController.getInstance().getNewMessages().addSnapshotListener(
                (value, error) -> {
                    assert value != null;
                    msgList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        MessageItem item = doc.toObject(MessageItem.class);
                        msgList.add(item);
                    }
                    Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
                }
        );
    }
}