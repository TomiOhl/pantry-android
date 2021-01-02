package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FamilyDaoImpl implements FamilyDao {

    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public Task<QuerySnapshot> exists(String family) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return db.collection("Users").whereEqualTo("family", family).get();
    }

    public Task<Void> setFamily(String userEmail, String familyEmail) {
        Map<String, Object> familyItem = new HashMap<>();
        familyItem.put("family", familyEmail);
        return db.collection("Users").document(userEmail).set(familyItem);
    }
}
