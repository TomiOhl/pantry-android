package com.tomi.ohl.szakdoga.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tomi.ohl.szakdoga.R;

public class MessagesViewHolder extends RecyclerView.ViewHolder {
    private CardView cardView;
    private TextView sender;
    private TextView date;
    private TextView content;

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.msgCard);
        sender = itemView.findViewById(R.id.msgSender);
        date = itemView.findViewById(R.id.msgDate);
        content = itemView.findViewById(R.id.msgContent);
    }

    public CardView getCardView() {
        return cardView;
    }

    public TextView getSender() {
        return sender;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getContent() {
        return content;
    }
}
