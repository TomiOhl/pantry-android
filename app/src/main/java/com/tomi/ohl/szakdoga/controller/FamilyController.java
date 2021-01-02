package com.tomi.ohl.szakdoga.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.dao.FamilyDao;
import com.tomi.ohl.szakdoga.dao.FamilyDaoImpl;

public class FamilyController {

    private FamilyDao dao = new FamilyDaoImpl();
    private static FamilyController instance = null;

    private FamilyController() {}

    public static FamilyController getInstance() {
        if (instance == null) {
            instance = new FamilyController();
        }
        return instance;
    }

    public Task<QuerySnapshot> exists(String family) {
        return dao.exists(family);
    }

    public Task<Void> setFamily(String userEmail, String familyEmail) {
        return dao.setFamily(userEmail, familyEmail);
    }
}
