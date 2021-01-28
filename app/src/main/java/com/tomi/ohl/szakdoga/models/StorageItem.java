package com.tomi.ohl.szakdoga.models;

public class StorageItem {
    private String name;
    private int count;
    private String location;
    private int shelf;
    private long date;

    public StorageItem() {
    }

    public StorageItem(String name, int count, String location, int shelf, long date) {
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

    public String getLocation() {
        return location;
    }

    public int getShelf() {
        return shelf;
    }

    public long getDate() {
        return date;
    }
}
