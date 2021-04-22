package com.tomi.ohl.szakdoga.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tomi.ohl.szakdoga.NewMessageActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.adapters.viewholders.MessagesViewHolder;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.utils.DateUtils;
import com.tomi.ohl.szakdoga.utils.DialogUtils;

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
                .inflate(R.layout.item_msg_list, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        Context ctx = holder.getCardView().getContext();
        String key = keys.get(position);
        MessageItem item = Objects.requireNonNull(msgs.get(key));
        String senderUid = item.getSenderUid();
        String sender = item.getSender();
        String content = item.getContent();
        holder.getSender().setText(sender);
        holder.getDate().setText(DateUtils.convertToDateAndTime(ctx, item.getDate()));
        holder.getContent().setText(content);
        holder.getCardView().setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && Objects.equals(user.getUid(), senderUid)) {
                contextMenu.add(R.string.edit).setOnMenuItemClickListener(menuItem -> {
                    Intent i = new Intent(ctx, NewMessageActivity.class);
                    i.putExtra("id", key);
                    i.putExtra("content", content);
                    ctx.startActivity(i);
                    return true;
                });
                contextMenu.add(R.string.delete).setOnMenuItemClickListener(menuItem -> {
                    DialogUtils.showConfirmDeleteMessageDialog(ctx, key);
                    return true;
                });
            }
        });
        // Az utolsó elem kap egy nagy paddinget a FAB miatt
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (position == getItemCount() -1) {
            DisplayMetrics displayMetrics = holder.getCardView().getResources().getDisplayMetrics();
            params.bottomMargin = (int) ((88 * displayMetrics.density) + 0.5);
        }
        else
            params.bottomMargin = 0;
        holder.itemView.setLayoutParams(params);
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
