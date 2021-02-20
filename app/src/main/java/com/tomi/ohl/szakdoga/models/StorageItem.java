package com.tomi.ohl.szakdoga.models;

public class StorageItem {
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
}
