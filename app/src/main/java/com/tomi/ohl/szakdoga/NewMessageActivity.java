package com.tomi.ohl.szakdoga;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.utils.DialogUtils;

public class NewMessageActivity extends AppCompatActivity {

    EditText newMessageEditText;
    private boolean messageSent;
    private String messageId;

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
        messageId = getIntent().getStringExtra("id");
        FloatingActionButton sendMessageFab = findViewById(R.id.fabSendMsg);

        if(messageId == null) {
            // Új üzenet lesz
            sendMessageFab.setOnClickListener(view -> sendMessage());
        } else {
            // Szerkeszteni jöttünk
            getSupportActionBar().setTitle(R.string.edit_msg);
            String oldText = getIntent().getStringExtra("content");
            newMessageEditText.setText(oldText);
            sendMessageFab.setOnClickListener(view -> editMessage());
        }

        // Legyen a bevitel fókuszban, hogy feljöjjön a billentyűzet
        newMessageEditText.requestFocus();
    }

    // A vissza gomb megnyomásánák művelete
    @Override
    public void onBackPressed() {
        if (messageId == null && !isEditTextEmpty())
            DialogUtils.showConfirmDiscardMessageDialog(this);
        else
            super.onBackPressed();
    }

    // Az ActionBar vissza nyilának megnyomásánák művelete
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Az alkalmazás bezárásakor elvégzendő műveletek
    @Override
    public void finish(){
        super.finish();
        // Bezáráskor animáció
        if (messageSent)
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out);
        else
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down);
    }

    // Az üzenet küldése gombot megnyomva új üzenet esetén
    private void sendMessage() {
        if (isEditTextEmpty())
            return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MessageItem msg = new MessageItem(
                    user.getUid(),
                    user.getDisplayName(),
                    newMessageEditText.getText().toString(),
                    System.currentTimeMillis()
            );
            StorageController.getInstance().insertNewMessage(msg);
            messageSent = true;
            finish();
        }
    }

    // Az üzenet küldése gombot megnyomva üzenet szerkesztése esetén
    private void editMessage() {
        if (isEditTextEmpty())
            return;
        StorageController.getInstance().editMessage(messageId, newMessageEditText.getText().toString());
        messageSent = true;
        finish();
    }

    // Üres-e a szövegdoboz
    private boolean isEditTextEmpty() {
        return newMessageEditText.getText().toString().trim().isEmpty();
    }

}