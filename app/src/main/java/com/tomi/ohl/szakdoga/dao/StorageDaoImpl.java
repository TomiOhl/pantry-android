package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
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

    // Elem szerkesztése egy tárolóban
    public void editStorageItem(String currentFamily, String id, int count, String name, int shelf) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("Storages").document(id).update(
                "count", count,
                "name", name,
                "shelf", shelf
        );
    }

    // Elem törlése egy tárolóból
    public void deleteStorageItem(String currentFamily, String id) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("Storages").document(id).delete();
    }

    // Elemek lekérése tárolóból
    public Query getStorageItems(String currentFamily, int location, String sortBy) {
        db = FirebaseFirestore.getInstance();
        return db.collection("Families").document(currentFamily).collection("Storages").whereEqualTo("location", location).orderBy(sortBy);
    }

    // Keresett elemek lekérése tárolóbol
    public Query searchStorageItems(String currentFamily, String query) {
        db = FirebaseFirestore.getInstance();
        return db.collection("Families").document(currentFamily).collection("Storages")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query+"z")
                .orderBy("name");
    }

    // Elem hozzáadása a bevásárlólistához
    @Override
    public void insertShoppingListItem(String currentFamily, ShoppingListItem item) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("ShoppingList").document().set(item);
    }

    // Elem szerkesztése a bevásárlólistán
    @Override
    public void editShoppingListItem(String currentFamily, String id, ShoppingListItem item) {
        db = FirebaseFirestore.getInstance();
        if (item.getName() == null && item.isChecked() == null) // Üres elem
            return;
        if (item.getName() == null) // Csak pipát kaptunk
            db.collection("Families").document(currentFamily).collection("ShoppingList").document(id).update("checked", item.isChecked());
        else if (item.isChecked() == null) // Csak nevet kaptunk
            db.collection("Families").document(currentFamily).collection("ShoppingList").document(id).update("name", item.getName());
        else // Teljes elemet kaptunk
            db.collection("Families").document(currentFamily).collection("ShoppingList").document(id).set(item);
    }

    // Elem törlése a bevásárlólistáról
    @Override
    public void deleteShoppingListItem(String currentFamily, String id) {
        db = FirebaseFirestore.getInstance();
        db.collection("Families").document(currentFamily).collection("ShoppingList").document(id).delete();
    }

    // Bevásárlólisra elemeinek lekérése
    @Override
    public Query getShoppingListItems(String currentFamily) {
        db = FirebaseFirestore.getInstance();
        return db.collection("Families").document(currentFamily).collection("ShoppingList").orderBy("checked").orderBy("name");
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
