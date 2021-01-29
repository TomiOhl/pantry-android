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

    EditText newMessageEditText;
    private boolean messageSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        overridePendingTransition(R.anim.slide_up, android.R.anim.fade_out);

        // Vissza nyíl az Actionbarra
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_down);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        newMessageEditText = findViewById(R.id.editTextNewMessage);
        FloatingActionButton sendMessageFab = findViewById(R.id.fabSendMsg);

        if(getIntent().getStringExtra("id") == null) {
            // Új üzenet lesz
            sendMessageFab.setOnClickListener(view -> sendMessage());
        } else {
            // Szerkeszteni jöttünk
            getSupportActionBar().setTitle(R.string.edit_msg);
            String id = getIntent().getStringExtra("id");
            String oldText = getIntent().getStringExtra("content");
            newMessageEditText.setText(oldText);
            sendMessageFab.setOnClickListener(view -> editMessage(id));
        }
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

    private void editMessage(String id) {
        if (newMessageEditText.getText().toString().trim().isEmpty())
            return;
        StorageController.getInstance().editMessage(id, newMessageEditText.getText().toString());
        messageSent = true;
        finish();
    }

}