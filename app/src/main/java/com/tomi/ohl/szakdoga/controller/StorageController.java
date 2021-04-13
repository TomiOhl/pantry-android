package com.tomi.ohl.szakdoga.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.dao.StorageDao;
import com.tomi.ohl.szakdoga.dao.StorageDaoImpl;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.models.SuggestionItem;

public class StorageController {
    private StorageDao dao = new StorageDaoImpl();
    private static StorageController instance = null;

    private StorageController() {}

    public static StorageController getInstance() {
        if (instance == null) {
            instance = new StorageController();
        }
        return instance;
    }

    public void insertStorageItem(StorageItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertStorageItem(currentFamily, item);
    }

    public void editStorageItem(String id, int count, String name, int shelf) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.editStorageItem(currentFamily, id, count, name, shelf);
    }

    public void deleteStorageItem(String id, String name) {
        // Először adjuk hozzá a javaslatokhoz
        SuggestionItem newSuggestion = new SuggestionItem(name, System.currentTimeMillis());
        insertSuggestionItem(newSuggestion);
        // Mehet a törlés
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteStorageItem(currentFamily, id);
    }

    public Query getStorageItems(int location, String sortBy) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getStorageItems(currentFamily, location, sortBy);
    }

    public Query searchStorageItems(String query) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.searchStorageItems(currentFamily, query);
    }

    public void insertSuggestionItem(SuggestionItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertSuggestionItem(currentFamily, item);
    }
    public void deleteSuggestionItem(String id) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteSuggestionItem(currentFamily, id);
    }
    public Task<QuerySnapshot> getSuggestionItems() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getSuggestionItems(currentFamily);
    }

    public void insertShoppingListItem(ShoppingListItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertShoppingListItem(currentFamily, item);
    }

    public void editShoppingListItem(String id, ShoppingListItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        String itemName = item.getName();
        if (itemName != null && itemName.trim().isEmpty())
            dao.deleteShoppingListItem(currentFamily, id);
        else
            dao.editShoppingListItem(currentFamily, id, item);
    }

    public void deleteShoppingListItem(String id) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteShoppingListItem(currentFamily, id);
    }

    public Query getShoppingListItems() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getShoppingListItems(currentFamily);
    }

    public Task<QuerySnapshot> getShoppingListItemsOnce(boolean shouldTheyBeChecked) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getShoppingListItemsOnce(currentFamily, shouldTheyBeChecked);
    }

    public void insertNewMessage(MessageItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertNewMessage(currentFamily, item);
    }

    public void editMessage(String id, String newContent) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.editMessage(currentFamily, id, newContent);
    }

    public void deleteMessage(String id) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteMessage(currentFamily, id);
    }

    public Query getNewMessages() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getNewMessages(currentFamily);
    }

    public Task<QuerySnapshot> getLastMessage() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getLastMessage(currentFamily);
    }
}
