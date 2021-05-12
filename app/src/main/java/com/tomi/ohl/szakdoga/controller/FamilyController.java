package com.tomi.ohl.szakdoga.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.dao.FamilyDao;
import com.tomi.ohl.szakdoga.dao.FamilyDaoImpl;

/**
 * A családok/háztartásokkal kapcsolatos műveleteket tartalmazó osztály.
 */
public class FamilyController {

    private FamilyDao dao = new FamilyDaoImpl();
    private static FamilyController instance = null;
    private static String currentFamily;

    private FamilyController() {}

    /**
     * Az osztály egy példányának lekérése. Egy időben csak egy létezhet belőle.
     * @return az osztály egy példánya.
     */
    public static FamilyController getInstance() {
        if (instance == null) {
            instance = new FamilyController();
        }
        return instance;
    }

    /**
     * @return a bejelentkezett felhasználó családja.
     */
    public String getCurrentFamily() {
        return currentFamily;
    }

    /**
     * A bejelentkezett felhasználó családjának beállítása a memóriában a gyorsabb elérésért.
     * @param family a felhasználó családja.
     */
    public void setCurrentFamily(String family) {
        currentFamily = family;
    }

    /**
     * Létezik-e az adott e-mail-címmel létrehozott család.
     * @param family egy család létrehozójának e-mail-címe.
     * @return egy task, amiből kinyerhető, hogy létezik-e az adott e-mail-címmel létrehozott család.
     */
    public Task<QuerySnapshot> exists(String family) {
        return dao.exists(family);
    }

    /**
     * @return egy task, amiből kinyerhető a bejelentkezett felhasználó családja.
     */
    public Task<DocumentSnapshot> getFamily() {
        return dao.getFamily();
    }

    /**
     * Egy felhasználó hozzáadása egy családhoz.
     * @param userEmail a felhasználó e-mail-címe.
     * @param familyEmail a család létrehozójának e-mail-címe.
     * @return egy task, ami beállítja a felhasználónak az adott családot.
     */
    public Task<Void> setFamily(String userEmail, String familyEmail) {
        return dao.setFamily(userEmail, familyEmail);
    }

    /**
     * A bejelentkezett felhasználó néhány családspecifikus adatának lekérése.
     * @return egy task, amiből kinyerhetőek a felhasználó családspecifikus adatai.
     */
    public Task<DocumentSnapshot> getAccountInfo() {
        return dao.getAccountInfo(getCurrentFamily());
    }

    /**
     * A bejelentkezett felhasználó legutóbbi üzenetnézésének idejének beállítása.
     * @param lastSeenTime egy task, ami beállítja, mikor nézte meg utoljára a bejelentkezett felhasználó az üzeneteket.
     */
    public void setLastSeenMessage(long lastSeenTime) {
        dao.setLastSeenMessage(getCurrentFamily(), lastSeenTime);
    }
}
