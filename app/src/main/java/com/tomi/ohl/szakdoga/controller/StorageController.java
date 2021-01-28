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

    public Query getStorageItems(String location) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getStorageItems(currentFamily, location);
    }

    public void insertNewMessage(MessageItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertNewMessage(currentFamily, item);
    }
    public Query getNewMessages() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getNewMessages(currentFamily);
    }
}
