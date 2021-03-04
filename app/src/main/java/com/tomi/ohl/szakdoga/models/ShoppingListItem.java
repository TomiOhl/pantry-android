package com.tomi.ohl.szakdoga.models;

public class ShoppingListItem {
    private String name;
    private Boolean checked;

    public ShoppingListItem() {
    }

    public ShoppingListItem(String name, Boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public Boolean isChecked() {
        return checked;
    }
}
