package com.tomi.ohl.szakdoga.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.tomi.ohl.szakdoga.dao.StorageDao;
import com.tomi.ohl.szakdoga.dao.StorageDaoImpl;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.StorageItem;

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

    public void insertTest() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertTest(currentFamily);
    }

    public Task<DocumentSnapshot> getTestInsert() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getTestInsert(currentFamily);
    }

    public void insertStorageItem(StorageItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertStorageItem(currentFamily, item);
    }

    public void editStorageItem(String id, int count, String name, int shelf) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.editStorageItem(currentFamily, id, count, name, shelf);
    }

    public void deleteStorageItem(String id) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteStorageItem(currentFamily, id);
    }

    public Query getStorageItems(int location, String sortBy) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getStorageItems(currentFamily, location, sortBy);
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
}
