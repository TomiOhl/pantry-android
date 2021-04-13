package com.tomi.ohl.szakdoga.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.NewMessageActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.MessagesRecyclerViewAdapter;
import com.tomi.ohl.szakdoga.controller.FamilyController;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.utils.NotificationUtils;
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

        // Hint megjelenítése, csak álló módban
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            TextView descTextView = layout.findViewById(R.id.msgDesc);
            descTextView.setVisibility(View.GONE);
        }

        // Új üzenet gomb
        FloatingActionButton newMessageFab = layout.findViewById(R.id.fabNewMsg);
        newMessageFab.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), NewMessageActivity.class)));

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMessages();
        markAsRead(System.currentTimeMillis());
    }

    // Üzenetek betöltése
    private void loadMessages() {
        // Üzenetek listája RecyclerView-n
        msgMap = new LinkedHashMap<>();
        rv = requireView().findViewById(R.id.msgRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(requireView().getContext()));
        rv.setAdapter(new MessagesRecyclerViewAdapter(msgMap));
        // Üzenetek listájának lekérése
        ((MainActivity)requireActivity()).getDbListeners().add(StorageController.getInstance().getNewMessages().addSnapshotListener(
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

    // Legutóbbi olvasás idejének elmentése
    private void markAsRead(long time) {
        NotificationUtils.clearNotifications(requireContext());
        FamilyController.getInstance().setLastSeenMessage(time);
        BadgeDrawable badge = ((MainActivity) requireActivity()).getBottomNavigation().getBadge(R.id.messages);
        if (badge != null)
            badge.setVisible(false);
    }
}