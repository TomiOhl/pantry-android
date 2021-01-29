package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.StorageItem;

import java.util.HashMap;
import java.util.Map;

public class StorageDaoImpl implements StorageDao {

    private FirebaseFirestore db;

    public void insertTest(String currentFamily) {
        db = FirebaseFirestore.getInstance();
        Map<String, Object> randomitem = new HashMap<>();
        randomitem.put("timestamp", System.currentTimeMillis());
        db.collection("Families").document(currentFamily).collection("Test").document("test").set(randomitem);
    }

    public Task<DocumentSnapshot> getTestInsert(String currentFamily) {
        db = FirebaseFirestore.getInstance();
        return db.collection("Families").document(currentFamily).collection("Test").document("test").get();
    }

    // Elem beszúrása egy tárolóba
    public void insertStorageItem(String currentFamily, StorageItem item) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("Storages").document().set(item);
    }

    // Elemek lekérése tárolóból
    public Query getStorageItems(String currentFamily, String location) {
        db = FirebaseFirestore.getInstance();
        return db.collection("Families").document(currentFamily).collection("Storages").whereEqualTo("location", location).orderBy("shelf");
    }

    // Új üzenet mentése az adatbázisba
    public void insertNewMessage(String currentFamily, MessageItem item) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("Messages").document().set(item);
    }

    // Üzenet szerkesztése az adatbázisban
    public void editMessage(String currentFamily, String id, String newContent) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("Messages").document(id).update("content", newContent);
    }

    // Üzenet törlése az adatbázisbol
    public void deleteMessage(String currentFamily, String id) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("Messages").document(id).delete();
    }

    // Üzenetek lekérése az adatbázisból
    public Query getNewMessages(String currentFamily) {
        db = FirebaseFirestore.getInstance();
        return db.collection("Families").document(currentFamily).collection("Messages").orderBy("date");
    }

}
