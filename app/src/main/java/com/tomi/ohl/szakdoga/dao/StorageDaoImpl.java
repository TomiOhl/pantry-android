package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StorageDaoImpl implements StorageDao {

    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public void insertTest() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Map<String, Object> randomitem = new HashMap<>();
        randomitem.put("timestamp", System.currentTimeMillis());
        assert user != null;
        db.collection("Users").document(user.getUid()).set(randomitem);
    }

    public Task<DocumentSnapshot> getTestInsert() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return db.collection("Users").document(user.getUid()).get();
    }

}
