package com.example.househomey;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    private String username;
    private ItemList itemList;
    private FirebaseFirestore db;

    private DocumentReference userRef;

    public User(FirestoreUpdateListener listener, String username) {
        db = FirebaseFirestore.getInstance();

        this.username = username;
        userRef = db.collection("user").document(username);

        itemList = new ItemList(listener, userRef.collection("item"));
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
