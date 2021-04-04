package com.tomi.ohl.szakdoga.models;

public class SuggestionItem {
    private String name;
    private long date;

    public SuggestionItem() {
    }

    public SuggestionItem(String name, long date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }
}
