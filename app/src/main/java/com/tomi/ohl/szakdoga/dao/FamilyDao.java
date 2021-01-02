package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface FamilyDao {
    Task<QuerySnapshot> exists(String family);
    Task<Void> setFamily(String userEmail, String familyEmail);
}
