package com.tomi.ohl.szakdoga.models;

public class ShoppingListItem {
    private String name;
    private boolean checked;

    public ShoppingListItem() {
    }

    public ShoppingListItem(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }
}
