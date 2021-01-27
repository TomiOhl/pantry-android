package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tomi.ohl.szakdoga.models.StorageItem;

public interface StorageDao {
    void insertTest(String currentFamily);
    Task<DocumentSnapshot> getTestInsert(String currentFamily);
    void insertStorageItem(String currentFamily, StorageItem item);
}
