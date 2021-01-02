package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface StorageDao {
    void insertTest();
    Task<DocumentSnapshot> getTestInsert();
}
