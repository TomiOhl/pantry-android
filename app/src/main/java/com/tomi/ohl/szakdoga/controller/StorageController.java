package com.tomi.ohl.szakdoga.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomi.ohl.szakdoga.dao.StorageDao;
import com.tomi.ohl.szakdoga.dao.StorageDaoImpl;
import com.tomi.ohl.szakdoga.models.MessageItem;
import com.tomi.ohl.szakdoga.models.ShoppingListItem;
import com.tomi.ohl.szakdoga.models.StorageItem;
import com.tomi.ohl.szakdoga.models.SuggestionItem;

/**
 * Egy adott családon/háztartáson belül eltárolandó adatokat kezelő osztály.
 */
public class StorageController {
    private StorageDao dao = new StorageDaoImpl();
    private static StorageController instance = null;

    /**
     * Itt tároljuk el futás közben a tárolókban lévő elemek rendezési sorrendjét, mivel ezt nem befolyásoják az Activityk/Fragmentek életciklusai.
     */
    private String sortStoragesBy;

    private StorageController() {}

    /**
     * Az osztály egy példányának lekérése. Egy időben csak egy létezhet belőle.
     * @return az osztály egy példánya.
     */
    public static StorageController getInstance() {
        if (instance == null) {
            instance = new StorageController();
        }
        return instance;
    }

    /**
     * @return a választott rendezési sorrend a tárolók elemeinek listázásánál.
     */
    public String getSortStoragesBy() {
        if (sortStoragesBy == null)
            return "name";
        return sortStoragesBy;
    }

    /**
     * Tárolók elemei rendezési sorrendjének beállítása a memóriában a gyorsabb elérésért.
     * @param sortStoragesBy a választott rendezési sorrend.
     */
    public void setSortStoragesBy(String sortStoragesBy) {
        this.sortStoragesBy = sortStoragesBy;
    }

    /**
     * Elem beszúrása egy tárolóba.
     * @param item a beszúrandó elem.
     */
    public void insertStorageItem(StorageItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertStorageItem(currentFamily, item);
    }

    /**
     * Elem szerkesztése egy tárolóban.
     * @param id az elem id-je.
     * @param count az elem új mennyisége.
     * @param name az elem új neve.
     * @param shelf az elem új elhelyezésének polca.
     */
    public void editStorageItem(String id, int count, String name, int shelf) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.editStorageItem(currentFamily, id, count, name, shelf);
    }

    /**
     * Elem törlése egy tárolóból, illetve hozzáadása a javaslatokhoz.
     * @param id az elem id-je.
     * @param name az elem neve.
     */
    public void deleteStorageItem(String id, String name) {
        // Először adjuk hozzá a javaslatokhoz
        SuggestionItem newSuggestion = new SuggestionItem(name, System.currentTimeMillis());
        insertSuggestionItem(newSuggestion);
        // Mehet a törlés
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteStorageItem(currentFamily, id);
    }

    /**
     * Elemek lekérése tárolóból.
     * @param location a tároló id-je.
     * @return egy query, ami visszaadja az elemek listáját.
     */
    public Query getStorageItems(int location) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getStorageItems(currentFamily, location, getSortStoragesBy());
    }

    /**
     * Keresett elemek lekérése tárolóból.
     * @param query a keresés szövege.
     * @return egy query, ami visszaadja az elemek listáját.
     */
    public Query searchStorageItems(String query) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.searchStorageItems(currentFamily, query);
    }

    /**
     * Elem hozzáadása a javaslatokhoz.
     * @param item a beszúrandó elem.
     */
    public void insertSuggestionItem(SuggestionItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertSuggestionItem(currentFamily, item);
    }

    /**
     * Elem törlése a javaslatokból.
     * @param id a törlendő elem id-je.
     */
    public void deleteSuggestionItem(String id) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteSuggestionItem(currentFamily, id);
    }

    /**
     * Javaslatok lekérése az adatbázisból.
     * @return egy task, amiből kinyerhetőek a javaslatok.
     */
    public Task<QuerySnapshot> getSuggestionItems() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getSuggestionItems(currentFamily);
    }

    /**
     * Elem hozzáadása a bevásárlólistához.
     * @param item a beszúrandó elem.
     */
    public void insertShoppingListItem(ShoppingListItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertShoppingListItem(currentFamily, item);
    }

    /**
     * Elem szerkesztése a bevásárlólistán.
     * Amennyiben az elem új neve üres, az elem törlésre kerül.
     * @param id az elem id-je.
     * @param item a szerkesztett elem.
     */
    public void editShoppingListItem(String id, ShoppingListItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        String itemName = item.getName();
        if (itemName != null && itemName.trim().isEmpty())
            dao.deleteShoppingListItem(currentFamily, id);
        else
            dao.editShoppingListItem(currentFamily, id, item);
    }

    /**
     * Elem törlése a bevásárlólistáról.
     * @param id a törlendő elem id-je.
     */
    public void deleteShoppingListItem(String id) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteShoppingListItem(currentFamily, id);
    }

    /**
     * Bevásárlólisra elemeinek lekérése az adatbázisból.
     * @return egy query, ami visszaadja az elemek listáját.
     */
    public Query getShoppingListItems() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getShoppingListItems(currentFamily);
    }

    /**
     * Bevásárlólista elemeinek egyszeri lekérése aszerint, ki vannak-e pipálva.
     * @param shouldTheyBeChecked kipipált elemekre vagyunk-e kíváncsiak.
     * @return egy task, amiből kinyerhetőek a kért elemek.
     */
    public Task<QuerySnapshot> getShoppingListItemsOnce(boolean shouldTheyBeChecked) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getShoppingListItemsOnce(currentFamily, shouldTheyBeChecked);
    }

    /**
     * Új üzenet mentése az adatbázisba.
     * @param item az új üzenet.
     */
    public void insertNewMessage(MessageItem item) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.insertNewMessage(currentFamily, item);
    }

    /**
     * Üzenet szerkesztése az adatbázisban.
     * @param id az üzenet id-je.
     * @param newContent az üzenet új tartalma.
     */
    public void editMessage(String id, String newContent) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.editMessage(currentFamily, id, newContent);
    }

    /**
     * Üzenet törlése az adatbázisból.
     * @param id a törlendő üzenet id-je.
     */
    public void deleteMessage(String id) {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        dao.deleteMessage(currentFamily, id);
    }

    /**
     * Üzenetek lekérése az adatbázisból.
     * @return egy task, amiből kinyerhetőek a család üzenetei.
     */
    public Query getNewMessages() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getNewMessages(currentFamily);
    }

    /**
     * Legutóbbi üzenet lekérése az adatbázisból.
     * @return egy task, amiből kinyerhető a családban legutóbb küldött üzenet.
     */
    public Task<QuerySnapshot> getLastMessage() {
        String currentFamily = FamilyController.getInstance().getCurrentFamily();
        return dao.getLastMessage(currentFamily);
    }
}
