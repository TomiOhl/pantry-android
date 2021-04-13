package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FamilyDaoImpl implements FamilyDao {

    // Az adatbázisban található kollekciók nevei
    private static final String COLLECTION_USERS = "Users";
    private static final String COLLECTION_ACCOUNT_INFO = "AccountInfo";

    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public Task<QuerySnapshot> exists(String family) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_USERS).whereEqualTo("family", family).get();
    }

    @Override
    public Task<DocumentSnapshot> getFamily() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_USERS).document(Objects.requireNonNull(user.getEmail())).get();
    }

    @Override
    public Task<Void> setFamily(String userEmail, String familyEmail) {
        Map<String, Object> familyItem = new HashMap<>();
        familyItem.put("family", familyEmail);
        return db.collection(COLLECTION_USERS).document(userEmail).set(familyItem);
    }

    @Override
    public Task<DocumentSnapshot> getAccountInfo(String currentFamily) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_USERS).document(Objects.requireNonNull(user.getEmail()))
                .collection(COLLECTION_ACCOUNT_INFO).document(currentFamily).get();
    }

    @Override
    public void setLastSeenMessage(String currentFamily, long lastSeenTime) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Map<String, Object> lastSeenItem = new HashMap<>();
        lastSeenItem.put("lastSeen", lastSeenTime);
        db.collection(COLLECTION_USERS).document(Objects.requireNonNull(user.getEmail()))
                .collection(COLLECTION_ACCOUNT_INFO).document(currentFamily).set(lastSeenItem, SetOptions.merge());
    }
}
