package com.tomi.ohl.szakdoga.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.controller.FamilyController;

/**
 * A családok/háztartásokkal kapcsolatos adatbázisműveleteket tartalmazó interfész.
 */
public interface FamilyDao {
    /**
     * @see FamilyController#exists(String family)
     */
    Task<QuerySnapshot> exists(String family);

    /**
     * @see FamilyController#getFamily()
     */
    Task<DocumentSnapshot> getFamily();

    /**
     * @see FamilyController#setFamily(String userEmail, String familyEmail)
     */
    Task<Void> setFamily(String userEmail, String familyEmail);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see FamilyController#getAccountInfo()
     */
    Task<DocumentSnapshot> getAccountInfo(String currentFamily);

    /**
     * @param currentFamily a bejelentkezett felhasználó családja.
     * @see FamilyController#setLastSeenMessage(long lastSeenTime)
     */
    void setLastSeenMessage(String currentFamily, long lastSeenTime);
}
