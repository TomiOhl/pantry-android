package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.models.SuggestionItem;

public class StorageDaoImpl implements StorageDao {

    // Az adatbázisban található kollekciók nevei
    private static final String COLLECTION_FAMILIES = "Families";
    private static final String COLLECTION_STORAGES = "Storages";
    private static final String COLLECTION_SUGGESTIONS = "Suggestions";
    private static final String COLLECTION_SHOPPINGLIST = "ShoppingList";
    private static final String COLLECTION_MESSAGES = "Messages";

    private FirebaseFirestore db;

    // Elem beszúrása egy tárolóba
    public void insertStorageItem(String currentFamily, StorageItem item) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_STORAGES).document().set(item);
    }

    // Elem szerkesztése egy tárolóban
    public void editStorageItem(String currentFamily, String id, int count, String name, int shelf) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_STORAGES).document(id).update(
                "count", count,
                "name", name,
                "shelf", shelf
        );
    }

    // Elem törlése egy tárolóból
    public void deleteStorageItem(String currentFamily, String id) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_STORAGES).document(id).delete();
    }

    // Elemek lekérése tárolóból
    public Query getStorageItems(String currentFamily, int location, String sortBy) {
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_STORAGES)
                .whereEqualTo("location", location).orderBy(sortBy);
    }

    // Keresett elemek lekérése tárolóból
    public Query searchStorageItems(String currentFamily, String query) {
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_STORAGES)
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query+"z")
                .orderBy("name");
    }

    // Elem hozzáadása a javaslatokhoz
    @Override
    public void insertSuggestionItem(String currentFamily, SuggestionItem item) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SUGGESTIONS).document().set(item);
    }

    // Elem törlése a javaslatokból
    @Override
    public void deleteSuggestionItem(String currentFamily, String id) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SUGGESTIONS).document(id).delete();
    }

    // Javaslatok lekérése az adatbázisból
    @Override
    public Task<QuerySnapshot> getSuggestionItems(String currentFamily) {
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SUGGESTIONS)
                .orderBy("date", Query.Direction.DESCENDING).get();
    }

    // Elem hozzáadása a bevásárlólistához
    @Override
    public void insertShoppingListItem(String currentFamily, ShoppingListItem item) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SHOPPINGLIST).document().set(item);
    }

    // Elem szerkesztése a bevásárlólistán
    @Override
    public void editShoppingListItem(String currentFamily, String id, ShoppingListItem item) {
        db = FirebaseFirestore.getInstance();
        if (item.getName() == null && item.isChecked() == null) // Üres elem
            return;
        if (item.getName() == null) // Csak pipát kaptunk
            db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SHOPPINGLIST).document(id)
                    .update("checked", item.isChecked());
        else if (item.isChecked() == null) // Csak nevet kaptunk
            db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SHOPPINGLIST).document(id)
                    .update("name", item.getName());
        else // Teljes elemet kaptunk
            db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SHOPPINGLIST).document(id).set(item);
    }

    // Elem törlése a bevásárlólistáról
    @Override
    public void deleteShoppingListItem(String currentFamily, String id) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SHOPPINGLIST).document(id).delete();
    }

    // Bevásárlólisra elemeinek lekérése
    @Override
    public Query getShoppingListItems(String currentFamily) {
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SHOPPINGLIST).orderBy("checked").orderBy("name");
    }

    // Bevásárlólista kipipált elemeinek egyszeri lekérése
    @Override
    public Task<QuerySnapshot> getShoppingListItemsOnce(String currentFamily, boolean shouldTheyBeChecked) {
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_SHOPPINGLIST)
                .whereEqualTo("checked", shouldTheyBeChecked).orderBy("name").get();
    }

    // Új üzenet mentése az adatbázisba
    public void insertNewMessage(String currentFamily, MessageItem item) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_MESSAGES).document().set(item);
    }

    // Üzenet szerkesztése az adatbázisban
    public void editMessage(String currentFamily, String id, String newContent) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_MESSAGES).document(id).update("content", newContent);
    }

    // Üzenet törlése az adatbázisbol
    public void deleteMessage(String currentFamily, String id) {
        db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_MESSAGES).document(id).delete();
    }

    // Üzenetek lekérése az adatbázisból
    public Query getNewMessages(String currentFamily) {
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_FAMILIES).document(currentFamily).collection(COLLECTION_MESSAGES).orderBy("date");
    }

}
