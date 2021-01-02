package com.tomi.ohl.szakdoga.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tomi.ohl.szakdoga.dao.StorageDao;
import com.tomi.ohl.szakdoga.dao.StorageDaoImpl;

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
        dao.insertTest();
    }

    public Task<DocumentSnapshot> getTestInsert() {
        return dao.getTestInsert();
    }
}
