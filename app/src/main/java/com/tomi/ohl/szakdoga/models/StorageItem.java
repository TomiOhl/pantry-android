package com.tomi.ohl.szakdoga.models;

import java.io.Serializable;

/**
 * Egy tárolóban tárolt elem modellje az adatbázisban.
 */
public class StorageItem implements Serializable {
    private String name;
    private int count;
    private int location;
    private int shelf;
    private long date;

    public StorageItem() {
    }

    public StorageItem(String name, int count, int location, int shelf, long date) {
        this.name = name;
        this.count = count;
        this.location = location;
        this.shelf = shelf;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getLocation() {
        return location;
    }

    public int getShelf() {
        return shelf;
    }

    public long getDate() {
        return date;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
