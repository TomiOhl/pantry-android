package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.StorageItem;

public interface StorageDao {
    void insertTest(String currentFamily);
    Task<DocumentSnapshot> getTestInsert(String currentFamily);
    void insertStorageItem(String currentFamily, StorageItem item);
    void editStorageItem(String currentFamily, String id, StorageItem item);
    void deleteStorageItem(String currentFamily, String id);
    Query getStorageItems(String currentFamily, String location);
    void insertNewMessage(String currentFamily, MessageItem item);
    void editMessage(String currentFamily, String id, String newContent);
    void deleteMessage(String currentFamily, String id);
    Query getNewMessages(String currentFamily);
}
