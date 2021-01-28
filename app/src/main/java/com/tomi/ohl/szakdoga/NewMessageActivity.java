package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.MessageItem;

import java.util.Objects;

public class NewMessageActivity extends AppCompatActivity {

    private boolean messageSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        overridePendingTransition(R.anim.slide_up, android.R.anim.fade_out);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_down);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton sendMessageFab = findViewById(R.id.fabSendMsg);
        sendMessageFab.setOnClickListener(view -> sendMessage());
    }

    // Az ActionBar vissza nyila mit csináljon
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Az alkalmazás bezárásakor elvégzendő műveletek
    // TODO: elveti az uzenetet? dialog
    @Override
    public void finish(){
        super.finish();
        // Bezáráskor animáció
        if (messageSent)
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out);
        else
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down);
    }

    // Az üzenet küldése gombot megnyomva
    private void sendMessage() {
        EditText newMessageEditText = findViewById(R.id.editTextNewMessage);
        if (newMessageEditText.getText().toString().trim().isEmpty())
            return;
        MessageItem msg = new MessageItem(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName(),
                newMessageEditText.getText().toString(),
                System.currentTimeMillis()
        );
        StorageController.getInstance().insertNewMessage(msg);
        messageSent = true;
        finish();
    }

}