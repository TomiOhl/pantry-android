package com.tomi.ohl.szakdoga;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MessagesFragment extends Fragment {

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

        // Új üzenet gomb
        FloatingActionButton newMessageFab = layout.findViewById(R.id.fabNewMsg);
        newMessageFab.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), NewMessageActivity.class)));

        return layout;
    }

}