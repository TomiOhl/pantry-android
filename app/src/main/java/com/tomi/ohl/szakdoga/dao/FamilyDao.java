package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface FamilyDao {
    // Current family
    Task<QuerySnapshot> exists(String family);
    Task<DocumentSnapshot> getFamily();
    Task<Void> setFamily(String userEmail, String familyEmail);

    // Account Info
    Task<DocumentSnapshot> getAccountInfo(String currentFamily);
    void setLastSeenMessage(String currentFamily, long lastSeenTime);
}
