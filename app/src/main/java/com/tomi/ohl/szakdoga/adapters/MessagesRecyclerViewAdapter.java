package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tomi.ohl.szakdoga.NewMessageActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.utils.DateUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private LinkedHashMap<String, MessageItem> msgs;
    private ArrayList<String> keys;

    public MessagesRecyclerViewAdapter(LinkedHashMap<String, MessageItem> list) {
        msgs = list;
        keys = new ArrayList<>(msgs.keySet());
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_msg, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        String key = keys.get(position);
        String sender = Objects.requireNonNull(msgs.get(key)).getSender();
        String content = Objects.requireNonNull(msgs.get(key)).getContent();
        holder.getSender().setText(sender);
        holder.getDate().setText(DateUtils.convertToDateAndTime(Objects.requireNonNull(msgs.get(key)).getDate()));
        holder.getContent().setText(content);
        holder.getCardView().setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && Objects.equals(user.getDisplayName(), sender)) {
                contextMenu.add(R.string.edit).setOnMenuItemClickListener(menuItem -> {
                    Context ctx = holder.getCardView().getContext();
                    Intent i = new Intent(ctx, NewMessageActivity.class);
                    i.putExtra("id", key);
                    i.putExtra("content", content);
                    ctx.startActivity(i);
                    return true;
                });
                contextMenu.add(R.string.delete).setOnMenuItemClickListener(menuItem -> {
                    StorageController.getInstance().deleteMessage(key);
                    return true;
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public void updateKeys(Set<String> newKeys) {
        keys.clear();
        keys.addAll(new ArrayList<>(newKeys));
        notifyDataSetChanged();
    }
}