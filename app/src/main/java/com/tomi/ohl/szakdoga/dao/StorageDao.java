package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.controller.StorageController;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.models.SuggestionItem;

/**
 * Egy adott családon/háztartáson belül eltárolandó adatokkal kapcsolatos adatbázisműveleteket tartalmazó interfész.
 */
public interface StorageDao {
    // Storages

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#insertStorageItem(StorageItem item)
     */
    void insertStorageItem(String currentFamily, StorageItem item);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#editStorageItem(String id, int count, String name, int shelf)
     */
    void editStorageItem(String currentFamily, String id, int count, String name, int shelf);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#deleteStorageItem(String id, String name)
     */
    void deleteStorageItem(String currentFamily, String id);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#getStorageItems(int location)
     */
    Query getStorageItems(String currentFamily, int location, String sortBy);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#searchStorageItems(String query)
     */
    Query searchStorageItems(String currentFamily, String query);

    // Suggestions

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#insertSuggestionItem(SuggestionItem item)
     */
    void insertSuggestionItem(String currentFamily, SuggestionItem item);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#deleteSuggestionItem(String id)
     */
    void deleteSuggestionItem(String currentFamily, String id);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#getSuggestionItems()
     */
    Task<QuerySnapshot> getSuggestionItems(String currentFamily);

    // Shopping list

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#insertShoppingListItem(ShoppingListItem item)
     */
    void insertShoppingListItem(String currentFamily, ShoppingListItem item);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#editShoppingListItem(String id, ShoppingListItem item)
     */
    void editShoppingListItem(String currentFamily, String id, ShoppingListItem item);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#deleteShoppingListItem(String id)
     */
    void deleteShoppingListItem(String currentFamily, String id);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#getShoppingListItems()
     */
    Query getShoppingListItems(String currentFamily);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#getShoppingListItemsOnce(boolean shouldTheyBeChecked)
     */
    Task<QuerySnapshot> getShoppingListItemsOnce(String currentFamily, boolean shouldTheyBeChecked);

    // Messages

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#insertNewMessage(MessageItem item)
     */
    void insertNewMessage(String currentFamily, MessageItem item);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#editMessage(String id, String newContent)
     */
    void editMessage(String currentFamily, String id, String newContent);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#deleteMessage(String id)
     */
    void deleteMessage(String currentFamily, String id);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#getNewMessages()
     */
    Query getNewMessages(String currentFamily);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see StorageController#getLastMessage()
     */
    Task<QuerySnapshot> getLastMessage(String currentFamily);
}
