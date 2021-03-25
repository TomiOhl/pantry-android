package com.tomi.ohl.szakdoga.dao;

import com.google.firebase.firestore.Query;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.models.StorageItem;

public interface StorageDao {
    // Storages
    void insertStorageItem(String currentFamily, StorageItem item);
    void editStorageItem(String currentFamily, String id, int count, String name, int shelf);
    void deleteStorageItem(String currentFamily, String id);
    Query getStorageItems(String currentFamily, int location, String sortBy);
    Query searchStorageItems(String currentFamily, String query);

    // Shopping list
    void insertShoppingListItem(String currentFamily, ShoppingListItem item);
    void editShoppingListItem(String currentFamily, String id, ShoppingListItem item);
    void deleteShoppingListItem(String currentFamily, String id);
    Query getShoppingListItems(String currentFamily);

    // Messages
    void insertNewMessage(String currentFamily, MessageItem item);
    void editMessage(String currentFamily, String id, String newContent);
    void deleteMessage(String currentFamily, String id);
    Query getNewMessages(String currentFamily);
}
