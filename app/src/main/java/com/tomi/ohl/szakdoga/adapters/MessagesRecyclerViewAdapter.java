package com.tomi.ohl.szakdoga.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.utils.DateUtils;

import java.util.ArrayList;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private ArrayList<MessageItem> msgs;

    public MessagesRecyclerViewAdapter(ArrayList<MessageItem> list) {
        msgs = list;
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
        holder.getSender().setText(msgs.get(position).getSender());
        holder.getDate().setText(DateUtils.convertToDateAndTime(msgs.get(position).getDate()));
        holder.getContent().setText(msgs.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }
}
