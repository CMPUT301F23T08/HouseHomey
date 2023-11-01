package com.example.househomey;

import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    private String username;
    private ItemList itemList;
    private FirebaseFirestore db;

    public User(String username) {
        db = FirebaseFirestore.getInstance();

        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ItemList getItemList() {
        return itemList;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }
}
